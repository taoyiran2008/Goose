package com.taoyr.widget.widgets.commonrv.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by taoyr on 2017/9/21.
 * 控制器的行为。
 * Controller不保存仍和数据，只根据输入的数据，决定UI如何展现
 */

public interface IController<T> {
    /**
     * 实体类根据自身需要展现的UI布局，创建相应的ViewHolder。
     */
    RecyclerView.ViewHolder create(ViewGroup parent);

    /**
     * 将数据绑定并展现到UI上。返回true，则表示消耗点了事件，不再使用关联的视图渲染
     */
    boolean bind(RecyclerView.ViewHolder viewHolder, T data);

    /**
     * 处理复杂的视图逻辑
     * 将数据绑定并展现到UI上(单个元素的UI显示，与所属的群组有关联逻辑。比如list分组后，显示字母索引)。
     */
    boolean bind(RecyclerView.ViewHolder viewHolder, RvAdapter<T> adapter, List<T> list, int position);

    /**
     * 手动释放资源。比如一些UI中包括一些大图的引用，如果不及时释放，可能会造成内存溢出。
     */
    void releaseResource(RecyclerView.ViewHolder viewHolder);

    void onSelected(T t, int position, RvAdapter<T> adapter);

    // 分组list的情况
    // 不使用char，因为char没有equalsIgnoreCase类似的功能。
    // 这个方法原定义在adapter中（显示分组信息的时候会用到），但是我们将adapter作了抽象，让其
    // 适应所有数据类型，因此不能写在其中。于是将它作为Callback定义到外部，让拥有数据源的外部
    // 去复写。这样如果不把callback传递给adapter，adapter对分组的位置不知情了。
    // 最优的方案还是放到ui controller中，抽象为ui相关的行为
    int getSectionPosition(List<T> list, String initial); // prefix letter
}
