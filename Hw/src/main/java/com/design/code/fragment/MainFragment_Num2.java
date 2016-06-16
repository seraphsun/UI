package com.design.code.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.design.code.R;
import com.design.code.view.menu.actionbar.right.ActionBarRight;
import com.design.code.view.menu.actionbar.right.MenuObject;
import com.design.code.view.refresh.recycler.adapter.AdapterInAlpha;
import com.design.code.view.refresh.recycler.adapter.AdapterInScale;
import com.design.code.view.refresh.recycler.animator.AnimatorBase;
import com.design.code.view.refresh.recycler.animator.AnimatorFadeIn;
import com.design.code.view.refresh.recycler.animator.AnimatorFadeInDown;
import com.design.code.view.refresh.recycler.animator.AnimatorFadeInLeft;
import com.design.code.view.refresh.recycler.animator.AnimatorFadeInRight;
import com.design.code.view.refresh.recycler.animator.AnimatorFlipInBottomX;
import com.design.code.view.refresh.recycler.animator.AnimatorFlipInLeftY;
import com.design.code.view.refresh.recycler.animator.AnimatorFlipInRightY;
import com.design.code.view.refresh.recycler.animator.AnimatorFlipInTopX;
import com.design.code.view.refresh.recycler.animator.AnimatorLanding;
import com.design.code.view.refresh.recycler.animator.AnimatorOvershootInLeft;
import com.design.code.view.refresh.recycler.animator.AnimatorOvershootInRight;
import com.design.code.view.refresh.recycler.animator.AnimatorScaleIn;
import com.design.code.view.refresh.recycler.animator.AnimatorScaleInLeft;
import com.design.code.view.refresh.recycler.animator.AnimatorScaleInRight;
import com.design.code.view.refresh.recycler.animator.AnimatorScaleInTop;
import com.design.code.view.refresh.recycler.animator.AnimatorSlideInDown;
import com.design.code.view.refresh.recycler.animator.AnimatorSlideInLeft;
import com.design.code.view.refresh.recycler.animator.AnimatorSlideInRight;
import com.design.code.view.refresh.recycler.animator.AnimatorSlideInUp;
import com.design.code.view.refresh.PullToRefresh;
import com.design.code.view.refresh.SwipeRefresh;
import com.design.code.view.widget.IndexListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ignacey 2016/1/11.
 */
public class MainFragment_Num2 extends Fragment implements ActionBarRight.OnMenuItemClickListener, ActionBarRight.OnMenuItemLongClickListener {

    private AppCompatActivity mActivity;
    private ViewFlipper mViewFlipper;

    private FragmentManager fragmentManager;
    private ActionBarRight mMenuDialogFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AppCompatActivity) activity;
        fragmentManager = mActivity.getSupportFragmentManager();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加菜单
        setHasOptionsMenu(true);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.main_fragment_num_2, null);
//
//        initView(view);
//
//        return view;
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化控件
     */
    private void initView(View view) {
        initToolbar(view);
        mViewFlipper = (ViewFlipper) view.findViewById(R.id.mViewFlipper);

        initView_num1(view);
        initView_num2(view);
        initView_num3(view);
    }

    /**
     * 初始化ActionBar
     */
    private void initToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((TextView) view.findViewById(R.id.toolbar_title)).setText("Wang Jun");
        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        initMenuFragment();
    }

    private void initMenuFragment() {
        MenuObject.MenuParams menuParams = new MenuObject.MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.dimen_56));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ActionBarRight.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)

        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)

        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.icon_toolbar_right);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.mipmap.icon_toolbar_right_1);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_toolbar_right_2);
        like.setBitmap(b);

//        MenuObject addFr = new MenuObject("Add to friends");
//        BitmapDrawable bd = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.mipmap.icon_toolbar_right_3));
//        addFr.setDrawable(bd);
//
//        MenuObject addFav = new MenuObject("Add to favorites");
//        addFav.setResource(R.mipmap.icon_toolbar_right_4);
//
//        MenuObject block = new MenuObject("Block user");
//        block.setResource(R.mipmap.icon_toolbar_right_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
//        menuObjects.add(addFr);
//        menuObjects.add(addFav);
//        menuObjects.add(block);
        return menuObjects;
    }

    /**
     * 初始化第一种refresh
     */
    private void initView_num1(View view) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        final SampleAdapter sampleAdapter = new SampleAdapter(mActivity, new ArrayList<String>());
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(ItemAnim.values()[(sampleAdapter.count) % 20].getAnimator());
        recyclerView.setAdapter(new AdapterInScale(new AdapterInAlpha(sampleAdapter)));

        final PullToRefresh pullToRefresh = (PullToRefresh) view.findViewById(R.id.pull_to_refresh);
        pullToRefresh.setOnRefreshListener(new PullToRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                        recyclerView.setItemAnimator(ItemAnim.values()[(sampleAdapter.count) % 20].getAnimator());
                        recyclerView.getItemAnimator().setAddDuration(1000);
                        recyclerView.getItemAnimator().setRemoveDuration(1000);
                        sampleAdapter.addItem(1);
                    }
                }, 2000);
            }
        });
    }

    /**
     * 初始化第二种refresh
     */
    private void initView_num2(View view) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_grid);
        final SampleAdapter sampleAdapter = new SampleAdapter(mActivity, new ArrayList<String>());
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(ItemAnim.values()[(sampleAdapter.count) % 20].getAnimator());
        recyclerView.setAdapter(new AdapterInScale(new AdapterInAlpha(sampleAdapter)));

        final SwipeRefresh swipeRefresh = (SwipeRefresh) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        recyclerView.setItemAnimator(ItemAnim.values()[(sampleAdapter.count) % 20].getAnimator());
                        recyclerView.getItemAnimator().setAddDuration(1000);
                        recyclerView.getItemAnimator().setRemoveDuration(1000);
                        sampleAdapter.addItem(1);
                    }
                }, 4000);
            }
        });
    }

    /**
     * 初始化IndexListView
     */
    private void initView_num3(View view) {
        ArrayList<String> mItems = new ArrayList<>();
        mItems.add("An investment in knowledge pays the best interest.");
        mItems.add("Fire proves gold, adversity proves men.");
        mItems.add("Keep your face to the sunshine, and you cannot see a shadow.");
        mItems.add("Happiness is a way station between too much and too little.");
        mItems.add("Attitude is a little thing that makes a big difference.");
        mItems.add("If opportunity doesn't knock, build a door.");
        mItems.add("To improve is to change, to be perfect is to change often.");
        mItems.add("Try to be a rainbow in someone’s cloud.");
        mItems.add("Stay hungry, stay foolish.");
        mItems.add("By failing to prepare, you are preparing to fail.");
        mItems.add("I am a slow walker, but I never walk back.");
        mItems.add("Great hopes make great man.");
        mItems.add("Follow your heart, but take your brain with you.");
        mItems.add("Fear is an essential part of our survival, It keeps us alert.");
        mItems.add("Genius often betrays itself into great errors.");
        mItems.add("The only thing worse than being talked about is not being talked about.");
        mItems.add("Everything has its time and that time must be watched.");
        mItems.add("The course of true love never did run smooth.");
        mItems.add("He that would command must serve.");
        mItems.add("Don't be a woman that needs a man, Be a woman a man needs.");
        mItems.add("Happiness takes no account of time.");
        mItems.add("Fair and softly go far in a day.");
        mItems.add("A book must be the axe for the frozen sea inside us.");

        Collections.sort(mItems);

        IndexListView listView = (IndexListView) view.findViewById(R.id.index_listview);
        listView.setAdapter(new IndexListAdapter(getActivity(), android.R.layout.simple_list_item_1, mItems));
        listView.setFastScrollEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), "position : " + position + ",position : " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_fragment_num_2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_menu_right:
                if (fragmentManager.findFragmentByTag(ActionBarRight.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ActionBarRight.TAG);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case 1:
                mViewFlipper.showPrevious();
                break;
            case 2:
                mViewFlipper.showNext();
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        mViewFlipper.showNext();
    }

    public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> {

        private Context mContext;
        private List<String> mDataSet;
        public int count = 100;

        public SampleAdapter(Context context, List<String> dataSet) {
            mContext = context;
            mDataSet = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_2_refresh_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(mContext).load(R.mipmap.wang_jun).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return count;
        }

//        public void removeItem(int position) {
//            mDataSet.remove(position);
//            notifyItemRemoved(position);
//        }

        public void addItem(int position) {
            count++;
            notifyItemInserted(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }

    private enum ItemAnim {
        FadeIn(new AnimatorFadeIn(new OvershootInterpolator(1f))),
        FadeInDown(new AnimatorFadeInDown(new OvershootInterpolator(1f))),
        FadeInUp(new AnimatorFadeInDown(new OvershootInterpolator(1f))),
        FadeInLeft(new AnimatorFadeInLeft(new OvershootInterpolator(1f))),
        FadeInRight(new AnimatorFadeInRight(new OvershootInterpolator(1f))),
        Landing(new AnimatorLanding(new OvershootInterpolator(1f))),
        ScaleIn(new AnimatorScaleIn(new OvershootInterpolator(1f))),
        ScaleInTop(new AnimatorScaleInTop(new OvershootInterpolator(1f))),
        ScaleInLeft(new AnimatorScaleInLeft(new OvershootInterpolator(1f))),
        ScaleInRight(new AnimatorScaleInRight(new OvershootInterpolator(1f))),
        FlipInTopX(new AnimatorFlipInTopX(new OvershootInterpolator(1f))),
        FlipInBottomX(new AnimatorFlipInBottomX(new OvershootInterpolator(1f))),
        FlipInLeftY(new AnimatorFlipInLeftY(new OvershootInterpolator(1f))),
        FlipInRightY(new AnimatorFlipInRightY(new OvershootInterpolator(1f))),
        SlideInLeft(new AnimatorSlideInLeft(new OvershootInterpolator(1f))),
        SlideInRight(new AnimatorSlideInRight(new OvershootInterpolator(1f))),
        SlideInDown(new AnimatorSlideInDown(new OvershootInterpolator(1f))),
        SlideInUp(new AnimatorSlideInUp(new OvershootInterpolator(1f))),
        OvershootInRight(new AnimatorOvershootInRight(1.0f)),
        OvershootInLeft(new AnimatorOvershootInLeft(1.0f));

        private AnimatorBase mAnimator;

        /**
         * 枚举的构造方法，只有声明了构造器，枚举的成员才可以定义各种对象关系
         *
         * @param animator 对应枚举的成员Value
         */
        ItemAnim(AnimatorBase animator) {
            mAnimator = animator;
        }

        public AnimatorBase getAnimator() {
            return mAnimator;
        }
    }

    private class IndexListAdapter extends ArrayAdapter<String> implements SectionIndexer {

        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        public IndexListAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            if (IndexListView.StringMatcher.match(String.valueOf(getItem(j).charAt(0)), String.valueOf(k)))
                                return j;
                        }
                    } else {
                        if (IndexListView.StringMatcher.match(String.valueOf(getItem(j).charAt(0)), String.valueOf(mSections.charAt(i))))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }
    }
}
