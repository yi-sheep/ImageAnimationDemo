package com.gaoxianglong.imageanimationdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 实现查看大图和滑动下一张
 * 按照阅读顺序理解代码
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageAdapter mImageAdapter; // recyclerView适配器
    private ArrayList<String> list; // 保存图片链接
    private RecyclerView mImage_recyclerView; // recyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // 初始化
    }

    private void init() {
        // 准备数据
        list = new ArrayList<>(); // 实例化一个数组列表
        // 图片链接
        list.add("https://mfiles.alphacoders.com/850/850911.jpg");
        list.add("https://mfiles.alphacoders.com/850/850794.jpg");
        list.add("https://images2.alphacoders.com/106/thumb-1920-1069786.jpg");
        list.add("https://images5.alphacoders.com/106/thumb-350-1068940.jpg");
        list.add("https://images3.alphacoders.com/106/thumb-350-1068790.jpg");
        list.add("https://images6.alphacoders.com/106/thumb-350-1068675.png");
        list.add("https://images8.alphacoders.com/106/thumb-350-1068668.png");
        list.add("https://images2.alphacoders.com/106/thumb-350-1068543.jpg");
        list.add("https://images6.alphacoders.com/106/thumb-350-1068341.png");
        list.add("https://images7.alphacoders.com/106/thumb-350-1068325.jpg");
        list.add("https://images2.alphacoders.com/106/thumb-350-1068324.jpg");

        // 对recyclerView做适配
        mImageAdapter = new ImageAdapter(R.layout.item_image, list); // 实例化适配器
        mImage_recyclerView = findViewById(R.id.image_recyclerView); // 实例化recyclerView控件
        mImage_recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2)); // 设置布局面板 这里设置的是网格布局一排放两个
        mImage_recyclerView.setAdapter(mImageAdapter); // 设置适配器

        // 设置点击事件
        mImageAdapter.setOnItemClickListener(((adapter, view, position) -> {
            ImageView item_iv = view.findViewById(R.id.item_image); // 初始化点击的item_image控件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                share(item_iv, position); // 将点击的子控件和它在recyclerView的位置共享给下一个activity
            }
        }));
    }

    /**
     * activity之间共享view
     *
     * @param view
     * @param position
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void share(View view, int position) {
        Intent intent = new Intent(MainActivity.this, ImageActivity.class); // 实例化一个显示意图
        intent.putStringArrayListExtra(ImageActivity.IMG_KEY, list); // 将数据列表传递到下一个activity
        intent.putExtra(ImageActivity.IMG_POSITION, position); // 把点击的图片索引位置传递过去
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, view, "share").toBundle(); // 制作场景过渡动画
        startActivity(intent, bundle); // 启动Activity
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            int exitPos = data.getIntExtra(ImageActivity.IMG_CURRENT_POSITION, -1);
            final View exitView = getExitView(exitPos);
            if (exitView != null) {
                ActivityCompat.setExitSharedElementCallback(this, new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        names.clear();
                        sharedElements.clear();
                        names.add(ViewCompat.getTransitionName(exitView));
                        sharedElements.put(Objects.requireNonNull(ViewCompat.getTransitionName(exitView)), exitView);
                        setExitSharedElementCallback(new SharedElementCallback() {
                        });
                    }
                });
            }
        }
    }

    private View getExitView(int position) {
        if (position == -1) {
            return null;
        }
        if (mImageAdapter != null) {
            return mImageAdapter.getViewByPosition(position, R.id.item_image);
        }
        return null;
    }

    class ImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public ImageAdapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, String s) {
            ImageView item_image = holder.getView(R.id.item_image);
            Glide.with(MainActivity.this).load(s).apply(new RequestOptions().centerCrop()).into(item_image);
        }
    }

}
