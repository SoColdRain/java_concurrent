# java_concurrent

> Java 并发编程学习笔记与思考
>
> 基于《Java并发编程实战》（Brian Goetz 等著）
>
> 学习方式：读书 + 手写 demo 验证 + 追问底层原理

---

## 一、核心概念理解

### 1. 可见性、原子性、有序性

| 问题 | 本质 | 解决手段 |
|------|------|---------|
| 可见性 | 一个线程的写，其他线程不一定立刻看到 | `volatile`、锁、`final` |
| 原子性 | 复合操作可能被中断，导致状态不一致 | 锁、`Atomic*` 类 |
| 有序性 | 编译器和处理器可能重排指令 | `volatile`、锁、`final` |

### 2. volatile 使用三条件

1. **单写多读**：只有一个线程写，多个线程读
2. **变量独立**：不与其他变量构成约束关系
3. **写操作不依赖旧值或外部状态**

`volatile` 管可见性 + 有序性；`synchronized` 管原子性 + 可见性 + 有序性。两者是**互补关系**，不是替代关系。

### 3. 指令重排序与 happens-before

- JVM 允许指令重排，只要单线程结果不变
- `volatile` 写禁止前面的写重排到它后面
- `volatile` 读禁止后面的读重排到它前面
- `final` 字段在构造器退出时"冻结"，禁止重排到发布之后
- `start()` 规则：start 之前的写 hb 子线程的动作（仅覆盖 start 前）

### 4. 对象的构造与发布

- **构造期间"别人看不见"**：是因为引用还没传出去（引用不可达），不是 JVM 锁住了对象
- **构造 return ≠ 跨线程可见**：需要 happens-before 桥（volatile / 锁 / final / static）
- **this 逸出**：构造未完成时引用泄露，打破"引用不可达"的前提
    - 形态一：匿名内部类隐式持有外部类引用（`this$0`）
    - 形态二：构造函数内调用可改写实例方法，触发多态分派
- **安全发布**：引用交给别的线程时，必须搭 hb 桥

---

## 二、线程安全设计策略

### 1. 不可变对象（Immutable）

不可变对象的四个条件：
1. 所有状态字段 `final`
2. 类 `final`（防止子类破坏）
3. 构造中 `this` 不逸出
4. 可变引用字段做防御性拷贝（构造时 + 返回时）

不可变性从根上消灭并发问题：状态不变 → 无竞态 → 无可见性问题 → 无需同步。
java

@Immutable

public final class OneValueCache {

private final BigInteger lastNumber;

private final BigInteger[] lastFactors;
纯文本
public OneValueCache(BigInteger lastNumber, BigInteger[] lastFactors) {
this.lastNumber = lastNumber;
this.lastFactors = Arrays.copyOf(lastFactors, lastFactors.length);
}

public BigInteger[] getLastFactors(BigInteger number) {
if (lastNumber == null || !lastNumber.equals(number) || lastFactors == null)
return null;
return Arrays.copyOf(lastFactors, lastFactors.length);
}
}
纯文本
### 2. 实例封闭（Instance Confinement）

把可变对象藏在类的私有字段里 + 不泄露引用 + 所有访问通过同步方法。
java

public class PersonSet {

private final Set<Person> mySet = new HashSet<>();
纯文本
public synchronized void addPerson(Person p) { mySet.add(p); }
public synchronized boolean containsPerson(Person p) { return mySet.contains(p); }
}
纯文本
`HashSet` 本身不是线程安全的，但被封闭在同步方法后面，整体线程安全。

### 3. 线程封闭

| 方式 | 说明 |
|------|------|
| Ad-hoc | 靠约定，不可靠 |
| 栈封闭 | 局部变量在栈上，线程私有 |
| ThreadLocal | 每个线程一份独立副本 |

### 4. 委托线程安全

将线程安全委托给已有的线程安全类（如 `AtomicLong`、`ConcurrentHashMap`）。

**注意**：委托只管"被委托对象内部的状态"，不管"引用本身"。所以引用通常需要 `final`：
java

private final AtomicLong count = new AtomicLong(0);
纯文本
`final` 保证引用不变（永远指向同一个实例），`AtomicLong` 保证内部操作原子。两者各管各的。

---

## 三、常见误区与纠偏

| 误区 | 真相 |
|------|------|
| 私有域 + 无 getter/setter 就线程安全 | 私有只防外部直接访问，内部方法照样能改，多线程调内部方法照样竞态 |
| 构造完成后对象"完全可见" | 构造 return 只保证单线程内状态一致，跨线程可见需要 hb 桥 |
| 主线程写完子线程一定看得到 | 需要同步动作搭桥，sleep/yield/println 只是碰巧帮忙 |
| 多个 Atomic 变量拼出跨变量原子性 | 各自原子 ≠ 跨变量不变式原子，中间存在窗口 |
| Servlet 自身被容器加锁 | 容器不帮 Servlet 加锁，无状态才是安全的原因 |
| deepCopy 是栈封闭 | 返回了新对象引用，是"每人一份不共享"，不是引用不出栈 |

---

## 四、关键代码

### VolatileCachedFactorizer（不可变对象 + volatile 发布）
java

@ThreadSafe

public class VolatileCachedFactorizer {

private volatile OneValueCache cache = new OneValueCache(null, null);
纯文本
public BigInteger[] service(BigInteger target) {
BigInteger[] factors = cache.getLastFactors(target);
if (factors == null) {
factors = CountUtil.factor(target);
cache = new OneValueCache(target, factors);
}
return factors;
}
}
纯文本
---

## 五、学习心得

- **"如果不是这样会怎样"** → 通过想象失败场景理解成功设计
- **故意写错看它怎么炸** → 比记住"这样写安全"结实一百倍
- **并发知识培养的是"状态思维"**：谁读、谁写、有没有可能被打断、引用会不会泄露
- **线程安全是一组设计选择**：不可变 → 封闭 → 委托 → 锁，能选轻的就不选重的

---

## 六、待深入

- [ ] `final` 字段 freeze 语义的 JVM 层面实现
- [ ] `synchronized` 锁升级过程
- [ ] `ConcurrentHashMap` 实现原理
- [ ] AQS（AbstractQueuedSynchronizer）
- [ ] 从并发到分布式：一致性、共识算法

---

*最后更新：2026-11-05*