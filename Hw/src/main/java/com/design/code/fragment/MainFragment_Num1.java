package com.design.code.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.design.code.R;
import com.design.code.activity.wheel.IosWheelDate;
import com.design.code.base.anim.Techniques;
import com.design.code.base.anim.YoYo;
import com.design.code.view.dialog.alert.IosDialogSheet;
import com.design.code.view.dialog.alert.SweetDialog;
import com.design.code.view.menu.actionbar.right.ActionBarRight;
import com.design.code.view.menu.actionbar.right.MenuObject;
import com.design.code.view.slider.SliderRelativeLayout;
import com.design.code.view.slider.anim.AnimBase;
import com.design.code.view.slider.anim.AnimDescription;
import com.design.code.view.slider.base.PagerBase;
import com.design.code.view.slider.base.PagerIndicator;
import com.design.code.view.slider.base.PagerInfiniteView;
import com.design.code.view.slider.type.InfiniteViewText;
import com.design.code.view.widget.SliderListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ignacey 2016/1/11.
 */
public class MainFragment_Num1 extends Fragment implements PagerInfiniteView.OnSliderClickListener, PagerBase.OnPageChangeListener, ActionBarRight.OnMenuItemClickListener, ActionBarRight.OnMenuItemLongClickListener {

    private SliderRelativeLayout mDemoSlider;
    private AppCompatActivity mActivity;

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

    @Override
    public void onResume() {
        super.onResume();
        mDemoSlider.startAutoCycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_num_1, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private int i = -1;

    private void initView() {
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        TextView title = (TextView) mActivity.findViewById(R.id.toolbar_title);
        title.setText("Wang Jun");

        // ShowTipsView
//        ShowTips showtips = new ShowTips.ShowTipsBuilder(mActivity)
//                .setTarget(title).setTitle("Do you Love me ?")
//                .setDescription("This is ToolBar for action")
//                .setDelay(500)
//                .setBackgroundColor(Color.parseColor("#000000"))
//                .build();
//
//        showtips.show(mActivity);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IosDialogSheet sheet = new IosDialogSheet(mActivity).builder();
                sheet.setTitle("Do you Love WangJunJun");
                sheet.addSheetItem("襁褓", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        mActivity.startActivity(new Intent(mActivity, IosWheelDate.class));
                    }
                });
                sheet.addSheetItem("弱冠", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        new SweetDialog(mActivity, SweetDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setCancelText("No,cancel plx!")
                                .setConfirmText("Yes,delete it!")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetDialog sDialog) {
                                        // reuse previous dialog instance, keep widget user state, reset them if you need
                                        sDialog.setTitleText("Cancelled!")
                                                .setContentText("Your imaginary file is safe :)")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setCancelClickListener(null)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetDialog.ERROR_TYPE);
                                    }
                                })
                                .setConfirmClickListener(new SweetDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetDialog sDialog) {
                                        sDialog.setTitleText("Deleted!")
                                                .setContentText("Your imaginary file has been deleted!")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setCancelClickListener(null)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                    }
                });
                sheet.addSheetItem("而立", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        new SweetDialog(mActivity, SweetDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("Sweet!")
                                .setContentText("Here's a custom image.")
                                .setCustomImage(R.drawable.sweet_dialog)
                                .show();
                    }
                });
                sheet.addSheetItem("不惑", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        new SweetDialog(mActivity, SweetDialog.ERROR_TYPE).setTitleText("Oops...").setContentText("Something went wrong!").show();
                    }
                });
                sheet.addSheetItem("知命", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        new SweetDialog(mActivity, SweetDialog.SUCCESS_TYPE).setTitleText("Good job!").setContentText("You clicked the button!").show();
                    }
                });
                sheet.addSheetItem("花甲", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        new SweetDialog(mActivity, SweetDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes,delete it!")
                                .setConfirmClickListener(new SweetDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetDialog sDialog) {
                                        // reuse previous dialog instance
                                        sDialog.setTitleText("Deleted!")
                                                .setContentText("Your imaginary file has been deleted!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                    }
                });
                sheet.addSheetItem("古稀", null, new IosDialogSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        final SweetDialog pDialog = new SweetDialog(mActivity, SweetDialog.PROGRESS_TYPE).setTitleText("Loading");
                        pDialog.show();
                        pDialog.setCancelable(false);
                        new CountDownTimer(800 * 7, 800) {
                            public void onTick(long millisUntilFinished) {
                                // you can change the progress bar color by ProgressHelper every 800 millis
                                i++;
                                switch (i) {
                                    case 0:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_ff0000));
                                        break;
                                    case 1:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_ff7d00));
                                        break;
                                    case 2:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_ffff00));
                                        break;
                                    case 3:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_00ff00));
                                        break;
                                    case 4:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_0000ff));
                                        break;
                                    case 5:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_00ffff));
                                        break;
                                    case 6:
                                        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.color_ff00ff));
                                        break;
                                }
                            }

                            public void onFinish() {
                                i = -1;
                                pDialog.setTitleText("Success!")
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetDialog.SUCCESS_TYPE);
                            }
                        }.start();
                    }
                });
                sheet.show();
            }
        });

        mActivity.setSupportActionBar(toolbar);
        mActivity.getSupportActionBar().setHomeButtonEnabled(true);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDemoSlider = (SliderRelativeLayout) getActivity().findViewById(R.id.slider_pager);

//        HashMap<String, String> url_maps = new HashMap<>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String, Integer> file_maps = new HashMap<>();
        file_maps.put("Big Bang Theory", R.mipmap.fragment_1_img_bigbang);
        file_maps.put("Hannibal", R.mipmap.fragment_1_img_hannibal);
        file_maps.put("House of Cards", R.mipmap.fragment_1_img_house);
        file_maps.put("Game of Thrones", R.mipmap.fragment_1_img_thrones);

        for (String name : file_maps.keySet()) {
            InfiniteViewText textSliderView = new InfiniteViewText(getActivity());
            // initialize a SliderRelativeLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(PagerInfiniteView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            // add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderRelativeLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderRelativeLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new AnimDescription());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        initMenuFragment();

//        initListView();
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

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.mipmap.icon_toolbar_right_3));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.mipmap.icon_toolbar_right_4);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.mipmap.icon_toolbar_right_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }

    private SliderListView listView;

//    private void initListView() {
//        listView = (SliderListView) getActivity().findViewById(R.id.transformers);
//        listView.setAdapter(new TransformerAdapter(getActivity()));
//
//        // step 1. create a MenuCreator
//        SliderListView.SliderCreator creator = new SliderListView.SliderCreator() {
//            @Override
//            public void create(SliderListView.SliderMenu menu) {
//                // create "open" item
//                SliderListView.SliderMenuItem openItem = new SliderListView.SliderMenuItem(getActivity());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
//                // set item width
//                openItem.setWidth(UtilDensity.dp2px(getActivity(), 90));
//                // set item title
//                openItem.setTitle("Open");
//                // set item title fontsize
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);
//
//                // create "delete" item
//                SliderListView.SliderMenuItem deleteItem = new SliderListView.SliderMenuItem(getActivity());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(UtilDensity.dp2px(getActivity(), 90));
//                // set a icon
//                deleteItem.setIcon(R.mipmap.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };
//
//        // set creator
//        listView.setMenuCreator(creator);
//
//        // step 2. listener item click event
//        listView.setOnMenuItemClickListener(new SliderListView.OnSliderItemClickListener() {
//            @Override
//            public void onItemClick(int position, SliderListView.SliderMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        // open
//                        break;
//                    case 1:
//                        // delete
//                        break;
//                }
//            }
//        });
//
//        // set SwipeListener
//        listView.setOnSwipeListener(new SliderListView.OnSliderListener() {
//
//            @Override
//            public void onSwipeStart(int position) {
//                // swipe start
//            }
//
//            @Override
//            public void onSwipeEnd(int position) {
//                // swipe end
//            }
//        });
//
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                mDemoSlider.setPresetTransformer(((TextView) (((FrameLayout) view).getChildAt(0))).getText().toString());
//                Toast.makeText(getActivity(), ((TextView) (((FrameLayout) view).getChildAt(0))).getText().toString(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
//
//        listView.setCloseInterpolator(new BounceInterpolator());
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_fragment_num_1, menu);
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
                mDemoSlider.setCustomAnimation(new ChildAnimationExampleAnim());
                type = ANIM_TYPE.Flipper;
                break;
            case 2:
                mDemoSlider.setCustomIndicator((PagerIndicator) getActivity().findViewById(R.id.slider_indicator_custom));
                type = ANIM_TYPE.Zooming;
                break;
            case 3:
                type = ANIM_TYPE.Bouncing;
                break;
            case 4:
                type = ANIM_TYPE.Fading;
                break;
            case 5:
                mDemoSlider.setPresetIndicator(SliderRelativeLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new AnimDescription());
                type = ANIM_TYPE.Slider;
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
    }

    @Override
    public void onStop() {
        super.onStop();
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
    }

    @Override
    public void onSliderClick(PagerInfiniteView slider) {
        Toast.makeText(getActivity(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * ListView多变速器选项列表的适配器
     */
//    public class TransformerAdapter extends BaseAdapter {
//
//        private Context mContext;
//        private int pixels;
//
//        public TransformerAdapter(Context context) {
//            mContext = context;
//            pixels = UtilDensity.dp2px(mContext, 10);
//        }
//
//        @Override
//        public int getCount() {
//            return SliderRelativeLayout.Transformer.values().length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return SliderRelativeLayout.Transformer.values()[position].toString();
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView = new TextView(mContext);
//            textView.setHeight(UtilDensity.dp2px(mContext, 50));
//            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//            textView.setPadding(pixels, pixels, pixels, pixels);
//            textView.setText(getItem(position).toString());
//            textView.setTextColor(getResources().getColor(R.color.color_228FC1));
//
//            return textView;
//        }
//    }

    ANIM_TYPE type = ANIM_TYPE.Attention;

    /**
     * indicator特效样例
     */
    public class ChildAnimationExampleAnim implements AnimBase {

        @Override
        public void onPrepareCurrentItemLeaveScreen(View current) {
            View descriptionLayout = current.findViewById(R.id.description_layout);
            if (descriptionLayout != null) {
                current.findViewById(R.id.description_layout).setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPrepareNextItemShowInScreen(View next) {
            View descriptionLayout = next.findViewById(R.id.description_layout);
            if (descriptionLayout != null) {
                next.findViewById(R.id.description_layout).setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onCurrentItemDisappear(View view) {
        }

        @Override
        public void onNextItemAppear(View view) {
            View descriptionLayout = view.findViewById(R.id.description_layout);

            if (descriptionLayout != null) {
                view.findViewById(R.id.description_layout).setVisibility(View.VISIBLE);

                switch (type) {
                    case Attention:
                        YoYo.with(Techniques.StandUp)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Bouncing:
                        YoYo.with(Techniques.BounceIn)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Fading:
                        YoYo.with(Techniques.FadeInDown)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Flipper:
                        YoYo.with(Techniques.FlipInX)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Rotating:
                        YoYo.with(Techniques.RotateInDownLeft)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Slider:
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Special:
                        YoYo.with(Techniques.RollIn)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    case Zooming:
                        YoYo.with(Techniques.ZoomIn)
                                .duration(1200)
                                .playOn(descriptionLayout);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public enum ANIM_TYPE {
        Attention,
        Bouncing,
        Fading,
        Flipper,
        Rotating,
        Slider,
        Special,
        Zooming
    }
}
