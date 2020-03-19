package com.gaoxianglong.imageanimationdemo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class ImageActivity extends AppCompatActivity {

    private List<String> imgList;
    private int currentPosition;
    private int enterPosition;

    public static final String IMG_KEY = "IMG_KEY";
    public static final String IMG_POSITION = "IMG_POSITION";
    public static final String IMG_CURRENT_POSITION = "IMG_CURRENT_POSITION";

    private PagerSnapHelper mSnapHelper;
    private ImageAdapter mImageAdapter;
    private RecyclerView item_image_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        init();
    }

    private void init() {
        item_image_recyclerView = findViewById(R.id.item_image_recyclerView);

        // 延迟共享动画的执行
        postponeEnterTransition();

        imgList = getIntent().getStringArrayListExtra(IMG_KEY);
        enterPosition = getIntent().getIntExtra(IMG_POSITION, 0);
        currentPosition = enterPosition;

        mSnapHelper = new PagerSnapHelper(){
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                currentPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                return currentPosition;
            }
        };
        mSnapHelper.attachToRecyclerView(item_image_recyclerView);

        mImageAdapter = new ImageAdapter(imgList);
        item_image_recyclerView.setLayoutManager(new LinearLayoutManager(ImageActivity.this, LinearLayoutManager.HORIZONTAL, false));
        item_image_recyclerView.setAdapter(mImageAdapter);

//        mImageAdapter.bindViewHolder();

        item_image_recyclerView.scrollToPosition(enterPosition);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (enterPosition != currentPosition) {
            // 滑动过，需要刷新
            View exitView = mImageAdapter.getViewByPosition(currentPosition, R.id.item_image);
            ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    names.clear();
                    sharedElements.clear();
                    names.add(ViewCompat.getTransitionName(exitView));
                    sharedElements.put(ViewCompat.getTransitionName(exitView), exitView);
                }
            });
        }
    }

    @Override
    public void finishAfterTransition() {
        Intent intent = new Intent();
        if (enterPosition == currentPosition) {
            // 没有变化
            intent.putExtra(IMG_CURRENT_POSITION, -1);
        } else {
            intent.putExtra(IMG_CURRENT_POSITION, currentPosition);
        }
        setResult(RESULT_OK);
        super.finishAfterTransition();
    }

    class ImageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        public ImageAdapter(List<String> list) {
            super(R.layout.item_image_see, list);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            ImageView item_image = helper.getView(R.id.item_image_see);
            Glide.with(ImageActivity.this).load(item).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    //图片加载完成的回调中，启动过渡动画
                    startPostponedEnterTransition();
                    return false;
                }
            }).into(item_image);
        }

    }
}
