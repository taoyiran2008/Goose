说明

作为常用，而且比较复杂（strings，drawable等资源文件内容较多）的控件，我们将其从widget大杂烩中单独抽离
出来。

作为独立的module，我们希望它能保持独立性（比如我们将代码中的PictureUtils.dip2px，LogMan.logError等置换
成自己的方法，而不去引用utility module），尽可能不去引用其他模块，从而确保，下次我们使用这个模组的时候，
引用它即可，而不需要把他依赖的模组也引用进来

