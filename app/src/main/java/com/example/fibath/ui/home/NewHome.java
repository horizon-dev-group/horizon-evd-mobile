package com.example.fibath.ui.home;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.example.fibath.R;
import com.example.fibath.databinding.ActivityNewHomeBinding;
import com.example.fibath.classes.user.User;
import com.example.fibath.ui.drawer.DrawerAdapter;
import com.example.fibath.ui.drawer.DrawerItem;
import com.example.fibath.ui.drawer.SimpleItem;
import com.example.fibath.ui.drawer.SpaceItem;
import com.example.fibath.ui.helpers.UIHelpers;
import com.example.fibath.ui.home.homeAdapters.NewHomeViewPagerAdapter;
import com.example.fibath.ui.profile.Profile;
import com.example.fibath.ui.setting.UserSetting;
import com.example.fibath.ui.statement.FundTransactionHistory;
import com.example.fibath.ui.transaction.VoucherTransactionHistory;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class NewHome extends LocalizationActivity implements DrawerAdapter.OnItemSelectedListener {
    private ActivityNewHomeBinding binding;

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

    public static SlidingRootNav drawer;
    // endregion

    // region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        try {
            setContentView(view);
            initUI();
            initDrawer(savedInstanceState);
        } catch (Exception exception) {

        }
    }
    // endregion

    // region Init UI
    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        UIHelpers.makeWindowTransparent(this);
        final NewHomeViewPagerAdapter adapter = new NewHomeViewPagerAdapter(this, getSupportFragmentManager(), 4);
        binding.homePageViewPager.setAdapter(adapter);
        binding.homePageViewPager.setOffscreenPageLimit(2);
        initEventHandlers();
    }

    private void initEventHandlers() {
        // region Bottom Navigation Dialog
        binding.vouchersBottomNavigation.addBubbleListener(id -> {
            switch (id) {
                case R.id.home_bottom_navigation_cards:
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
                    Intent i = new Intent("REFRESH_CARDS_PAGE");
                    lbm.sendBroadcast(i);
//                    Toast.makeText(getApplicationContext(), "Selected Page: " + 1, Toast.LENGTH_LONG).show();
                    binding.homePageViewPager.setCurrentItem(0);
                    return;
                case R.id.home_bottom_navigation_agnets:
//                    Toast.makeText(getApplicationContext(), "Selected Page: " + 2, Toast.LENGTH_LONG).show();
                    binding.homePageViewPager.setCurrentItem(1);
                    return;
                case R.id.home_bottom_navigation_transfers:
//                    Toast.makeText(getApplicationContext(), "Selected Page: " + 3, Toast.LENGTH_LONG).show();
                    binding.homePageViewPager.setCurrentItem(2);
                    return;
                case R.id.home_bottom_navigation_report:
//                    Toast.makeText(getApplicationContext(), "Selected Page: " + 4, Toast.LENGTH_LONG).show();
                    binding.homePageViewPager.setCurrentItem(3);
                    return;
                default:
                    binding.homePageViewPager.setCurrentItem(0);
            }
        });
        // endregion

        // region ViewPager Event Handler
        binding.homePageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.vouchersBottomNavigation.setSelected(position, false);
//                Toast.makeText(getApplicationContext(), "Selected Page: " + position, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // endregion
    }
    // endregion

    // region Drawer Implementation
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

        DrawerAdapter drawerAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(DRAWER_MENU_ITEM_DASHBOARD).setChecked(true),
                createItemFor(DRAWER_MENU_ITEM_TRANSACTIONS),
                createItemFor(DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY),
//                createItemFor(DRAWER_MENU_ITEM_Bank),
                new SpaceItem(24),
                createItemFor(DRAWER_MENU_ITEM_MY_ACCOUNT),
                createItemFor(DRAWER_MENU_ITEM_SETTINGS),
                createItemFor(DRAWER_MENU_ITEM_LOGOUT)
        ));
        drawerAdapter.setListener(this);

        TextView loggedInUserName = findViewById(R.id.drawer_user_name);
        loggedInUserName.setText(User.getUserSessionUsername());

        RecyclerView drawerMenuItemList = findViewById(R.id.drawer_side_menus_container);
        drawerMenuItemList.setNestedScrollingEnabled(false);
        drawerMenuItemList.setHasFixedSize(false);
        drawerMenuItemList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        drawerMenuItemList.setAdapter(drawerAdapter);

        drawerAdapter.setSelected(DRAWER_MENU_ITEM_DASHBOARD);
    }
    // endregion

    // region Drawer Menu Select On Click Listener
    @Override
    public void onItemSelected(int position) {
        if (position == DRAWER_MENU_ITEM_MY_VOUCHERS_HISTORY) {
            startActivity(new Intent(getApplicationContext(), FundTransactionHistory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_TRANSACTIONS) {
            startActivity(new Intent(getApplicationContext(), VoucherTransactionHistory.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_MY_ACCOUNT) {
            startActivity(new Intent(getApplicationContext(), Profile.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_SETTINGS) {
            startActivity(new Intent(getApplicationContext(), UserSetting.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (position == DRAWER_MENU_ITEM_LOGOUT) {
            User user = new User();
            user.logout(getApplicationContext());
        }
        drawer.closeMenu();
    }
    // endregion

    // region Drawer Item Create
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
                icons[i] = ContextCompat.getDrawable(getApplicationContext(), id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(getApplicationContext(), res);
    }
    // endregion
    // endregion

    // region Activity Overrides
    // region On Back Pressed Implementation
    @Override
    public void onBackPressed() {
        if (binding.homePageViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            binding.homePageViewPager.setCurrentItem(binding.homePageViewPager.getCurrentItem() - 1);
        }
    }
    // endregion

    @Override
    public void onResume() {
        super.onResume();
//        if (StateManager.isLanguageChanged()) {
//            StateManager.setLanguageChanged(false);
//            finish();
//            overridePendingTransition(0, 0);
//            startActivity(getIntent());
//            overridePendingTransition(0, 0);
//        }
    }
    // endregion
}