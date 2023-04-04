package com.example.fibath.ui.home;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.fibath.R;import com.example.fibath.classes.user.User;

import com.example.fibath.ui.drawer.DrawerAdapter;
import com.example.fibath.ui.drawer.DrawerItem;
import com.example.fibath.ui.drawer.SimpleItem;
import com.example.fibath.ui.drawer.SpaceItem;
import com.example.fibath.ui.home.fragments.DashboardFragment;
import com.example.fibath.ui.profile.Profile;
import com.example.fibath.ui.setting.UserSetting;
import com.example.fibath.ui.statement.FundTransactionHistory;
import com.example.fibath.ui.transaction.VoucherTransactionHistory;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    // region Initializations
    // region Drawer Items

    private static final int DRAWER_MENU_ITEM_DASHBOARD = 0;
    private static final int DRAWER_MENU_ITEM_TRANSACTIONS = 1;
    private static final int DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY = 2;
//    private static final int DRAWER_MENU_ITEM_Bank = 3;

    private static final int DRAWER_MENU_ITEM_MY_ACCOUNT = 4;
    private static final int DRAWER_MENU_ITEM_SETTINGS = 5;
    private static final int DRAWER_MENU_ITEM_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav drawer;
    // endregion

    // region UI Elements
    @BindView(R.id.activity_home_material_view_pager)
    MaterialViewPager materialViewPagerMainContainer;
    // endregion
    // endregion
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.out.println("User Boolean Is " + User.isIsFirstTime());
        ButterKnife.bind(this);
        textView.setText("dsfdsf");
        initUI(savedInstanceState);

    }

    // region Initialize UI
    private void initUI(Bundle savedInstanceState) {
        User user = new User();
        user.userBalance(getApplicationContext(), User.getUserSessionToken());
        initMaterialViewPagerMainContainer();
        initDrawer(savedInstanceState);
    }
    // endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// todo: goto back activity from here
            drawer.openMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // region Separate Component Initializations
    // region Init Material View Pager
    private void initMaterialViewPagerMainContainer() {
        materialViewPagerMainContainer.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public androidx.fragment.app.Fragment getItem(int position) {
                System.out.println("POSITION: " + position); // debug
                switch (position % 4) {
                    case 0:
                        return DashboardFragment.newInstance();
                    case 1:
                        return DashboardFragment.newInstance();
                    default:
                        return DashboardFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                        return "Dashboard";
                    case 1:
                        return "Activity";
                    case 2:
                        return "Report";
                    case 3:
                        return "Settings";
                }
                return "";
            }
        });

        materialViewPagerMainContainer.setMaterialViewPagerListener(page -> {
            switch (page) {
                case 0:
                    return HeaderDesign.fromColorResAndDrawable(
                            R.color.colorPrimary, getResources().getDrawable(R.drawable.bg_drawer_img_7));
                case 1:
                    return HeaderDesign.fromColorResAndDrawable(
                            R.color.colorPrimary, getResources().getDrawable(R.drawable.bg_home_screen_img_2));
                case 2:
                    return HeaderDesign.fromColorResAndDrawable(
                            R.color.colorPrimary, getResources().getDrawable(R.drawable.bg_drawer_img_2));
                case 3:
                    return HeaderDesign.fromColorResAndDrawable(
                            R.color.colorPrimary, getResources().getDrawable(R.drawable.bg_drawer_img_4));
            }

            //execute others actions if needed (ex : modify your header logo)

            return null;
        });

        // materialViewPagerMainContainer.getToolbar().g

        Toolbar toolbar = materialViewPagerMainContainer.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        materialViewPagerMainContainer.getViewPager().setOffscreenPageLimit(materialViewPagerMainContainer.getViewPager().getAdapter().getCount());
        materialViewPagerMainContainer.getPagerTitleStrip().setViewPager(materialViewPagerMainContainer.getViewPager());

        final View logo = findViewById(R.id.activity_home_text_view_title);
        if (logo != null) {
            logo.setOnClickListener(v -> {
                materialViewPagerMainContainer.notifyHeaderChanged();
            });
        }
    }
    // endregion

    // region Init Drawer
    private void initDrawer(Bundle savedInstanceState) {
        drawer = new SlidingRootNavBuilder(this)
//                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_side_menus)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        // Glide.with(this).load(R.drawable.bg_drawer_img).into(new SimpleTarget<Drawable>() {
        //     @Override
        //     public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
        //         findViewById(R.id.drawer_background_image).setBackground(resource);
        //     }
        // });

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(DRAWER_MENU_ITEM_DASHBOARD).setChecked(true),
                createItemFor(DRAWER_MENU_ITEM_TRANSACTIONS),
                createItemFor(DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY),
//                createItemFor(DRAWER_MENU_ITEM_Bank),
                new SpaceItem(24),
                createItemFor(DRAWER_MENU_ITEM_MY_ACCOUNT),
                createItemFor(DRAWER_MENU_ITEM_SETTINGS),
                createItemFor(DRAWER_MENU_ITEM_LOGOUT)
        ));
        adapter.setListener(this);
        TextView nameText = findViewById(R.id.drawer_user_name);
        nameText.setText(User.getUserSessionUsername());
        RecyclerView list = findViewById(R.id.drawer_side_menus_container);
        list.setNestedScrollingEnabled(false);
        list.setHasFixedSize(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(DRAWER_MENU_ITEM_DASHBOARD);
    }
    // endregion
    // endregion

    // region Drawer Implementations
    @Override
    public void onItemSelected(int position) {
        if (position == DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), FundTransactionHistory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_TRANSACTIONS) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), VoucherTransactionHistory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_MY_ACCOUNT) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_SETTINGS) {
            getApplicationContext().startActivity(new Intent(getApplicationContext(), UserSetting.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
//        else if (position == DRAWER_MENU_ITEM_Bank) {
////            getApplicationContext().startActivity(new Intent(getApplicationContext(), DownloadedVoucher.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//        }

        else if (position == DRAWER_MENU_ITEM_LOGOUT) {
            User user = new User();
            user.logout(getApplicationContext());
        }
        drawer.closeMenu();
    }
    // endregion

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Intent intent = new Intent(getApplicationContext(), Voucher.class);
            Intent intent = new Intent(getApplicationContext(), NewHome.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
