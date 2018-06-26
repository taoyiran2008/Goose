说明

为了能够下次构建新的APP时，快速集成，我们将App架构相关的一些公用类进行抽出，包括：
dagger injection 注入框架
Rxbus 模块间通信框架
rxjava、retrofit、okhttp 网络访问框架
以及一些公用方法的封装

module名为base，也可以命名structure, infrastructure，表示为App建筑的基座。