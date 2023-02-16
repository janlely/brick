# 源于函数式编程思想的Java编程方式
brick的目标是解决Java业务编程中的代码质量控制的问题，其核心思想是将程序看成是流程(Flow)和计算(Function)的组合，流程本身也是流程与计算的组合。 使用brick来编写业务代码可以有以下几个好处：
* 设计即代码：业务的详细设计本质上是流程设计，FlowMaker以写代码的方式创建流程
* 让专家来设计流程，让程序员来写实现：用FlowMaker在创建流程的时候，可以不写具体实现(return null)，只写流程。
* 方便进行单元测试：由于已经进行了拆分，测试非常方便。
* 避免写出屎山：
    * 使用FlowMaker创建的代码可以清晰的看出业务逻辑的具体流程
    * 纯函数与副作用分离的方式，可以进一步减少代码的复杂性
* 流程复用：可以由专家来对可以进行高度抽象的业务逻辑用FlowMaker进行流程抽象，封装成接口用于共享，例如[importFlow](https://github.com/janlely/brick/blob/main/brick-lib/src/main/java/org/brick/lib/importflow/IImportFlow.java)

# FlowMaker
要求使用FlowMaker来创建一个流程，利用Buidler来向流程中添加计算或者子流程。brick认为分支、循环等控制逻辑本质上也可以建模成流程，并提供了YesNoBranchFlow,loop,abort,MultiBranchFlow等类或方法来处理程序中的控制逻辑。FlowMaker还提供了一些如MapReduce的基本抽象。FlowMaker也使用了类型检测：要求每一步的输入类型必须是上一步的输出类型。 下面是一个简单的伪代码的例子（具体的例子在源码中）:
```java
Flow<Input, Output, Context> flow = new FlowMaker<Input, Output, Context>("某个业务逻辑的主流程")
  .asyncExecutor(Executors.newSingleThreadExecutor()) //流程中可以有异步，需要添加异步执行器
  .flowBuidler() //流程的Buidler
  .pure(new PureFunction("添加一个纯计算", (i,c) -> ...)) //pure方法添加一个无副作用的纯计算
  .flow(new FlowMaker<I,O,C>("子流程").flowBuilder().pure(...).flow(...).build()) //flow方法用于添加一个子流程
  .flow(new YesNoBranch<>("一个if-else分支",  //if-else分支本质上也是流程
      (i,c)- > ..., //分支判断逻辑
      new FlowMaker<I,O,C>.flowBuilder()....build(), //yes分支
      new FlowMaker<I,O,C>.flowBuilder()....build()))  //no分支
  .flow(new CaseBranch<>(           //switch-case分支流程
        "Sample CaseBranch",
        i -> i % 2 == 0 ? 1 : 2,    //case 值
        new CaseFlow<>(1, case1),   //case 1的分支
        new CaseFlow<>(2, case2)))  //case 2的分支
  .effect(new SideEffect("添加一个有副作用的计算", (i,c) -> ...))
  .abort(new AbortWhenFlow("一个if-return分支")) //添加一个if-return分支
  .flowAsync(...) //添加一个异步流程
  .loop(new LoopFlow<>("一个循环的流程",
      (i,c) -> .., //流程的终止条件
      someFlow, //用于循环执行的子流程
      collector)) //收集每一次循环结果的收集器
  .build()
```


# FlowTester
用于进行测试的工具，可以测试一个计算单元，也可以把多个计算单元连接起来测试，例子如下：
```java
public void testHelloWorld() {
    HelloWorldFlow.HelloContext context = new HelloWorldFlow.HelloContext();
    HelloWorldFlow.HelloRequest req = new HelloWorldFlow.HelloRequest();
    req.setName("jay");
    assert new FlowTester<Void, String, Pair<HelloWorldFlow.HelloRequest, HelloWorldFlow.HelloContext>>()
            .linkUnit(helloWorldFlow.getFirstStep()) //测试一个计算单元
            .pass(s -> StringUtils.equals(s, "jay"))
            .build()
            .run(null, new Pair(req, context));

    assert new FlowTester<Void, String, Pair<HelloWorldFlow.HelloRequest, HelloWorldFlow.HelloContext>>()
            .linkUnit(helloWorldFlow.getFirstStep()) //第一个计算单元
            .linkUnit(helloWorldFlow.getSecondStep()) //连接另一个计算单元
            .pass(s -> StringUtils.equals(s, "yaj")) //测试是否通过的判断逻辑
            .build().run(null, new Pair<>(req, context));
}
```


