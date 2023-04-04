package com.example.fibath.ui.home.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.example.fibath.R;import com.example.fibath.ui.home.adapters.menu.DashboardMenuAdapter;
import com.example.fibath.ui.home.adapters.menu.Menu;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Recent Transactions Initializations
    TableView mTableView;
    // endregion

    // region Menus Container
    TextView moreItems;
    RecyclerView menusContainer;
    private boolean isMoreToolsShown = false;
    // endregion

    // region

    // region Util Initializations
    private static List<Menu> dashboardMenus;
    private static DashboardMenuAdapter menusContainerAdapter;
    private static int spanCount;
    private Context context;
    // endregion

    private static View view;

    private List<Object> contents;

    private static final int GLANCE = 0;
    private static final int MAIN_MENU = 1;
    private static final int RECENT = 2;

    // region Constructor
    public DashboardFragmentAdapter(List<Object> contents) {
        spanCount = 3;
        this.contents = contents;
    }
    // endregion

    // region Get View Type
    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return GLANCE;
            case 1:
                return MAIN_MENU;
            default:
                return RECENT;
        }
    }
    // endregion

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = null;
        context = parent.getContext();
        switch (viewType) {
            case GLANCE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_home_dashboard_account_quick_info, parent, false);
                return new RecyclerView.ViewHolder(view) {

                };
            }
            case MAIN_MENU: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_dashboard_menus, parent, false);
//                TextView moreTools = view.findViewById(R.id.fragment_home_dashboard_menus_more_tools);
//                moreTools.setOnClickListener(view -> {
//                    showMoreTools();
//                    Toast.makeText(view.getContext(), "cl", Toast.LENGTH_LONG).show();
//                });
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case RECENT: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_dashboard_account_recent_transactions, parent, false);
                return new RecyclerView.ViewHolder(view) {

                };
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case GLANCE:
                break;
            case MAIN_MENU:
                initMainMenuItems();
                break;
//            case RECENT:
//                initRecentTransactions();
//                break;
        }

        initUI();
    }

    // region Initialize UI
    private void initUI() {
        // initMenuItems();
    }
    // endregion

    // region Separate Component Initializations
    // region Init Home Menus
    private void initMainMenuItems() {
        // region UI Elements
        //    @BindView(R.id.fragment_home_dashboard_menus_container)
        menusContainer = view.findViewById(R.id.fragment_home_dashboard_menus_container);

        dashboardMenus = new ArrayList<>();
        menusContainerAdapter = new DashboardMenuAdapter(view.getContext(), dashboardMenus);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(view.getContext(), spanCount);
        menusContainer.setLayoutManager(mLayoutManager);
        menusContainer.setAdapter(menusContainerAdapter);
        prepareMenus();
    }

    private void prepareMenus() {
        String vouchersTitle = context.getResources().getString(R.string.home_screen_dashboard_menu_item_voucher);
        String vouchersDescription = context.getResources().getString(R.string.home_screen_dashboard_menu_item_voucher_description);

        String topupTitle = context.getResources().getString(R.string.home_screen_dashboard_menu_item_topup);
        String topupDescription = context.getResources().getString(R.string.home_screen_dashboard_menu_item_topup_description);

        String fundsTitle = context.getResources().getString(R.string.home_screen_dashboard_menu_item_electricity_bill);
        String fundsDescription = context.getResources().getString(R.string.home_screen_dashboard_menu_item_electricity_bill_description);

        String transactionsTitle = context.getResources().getString(R.string.home_screen_dashboard_menu_item_water_bill);
        String transactionsDescription = context.getResources().getString(R.string.home_screen_dashboard_menu_item_water_bill_description);

        String downloadsTitle = "Admin";
        String downloadsDescription = "Sub Admin";

        String activityTitle = context.getResources().getString(R.string.home_screen_dashboard_menu_item_withdraw);
        String activityDescription = context.getResources().getString(R.string.home_screen_dashboard_menu_item_withdraw_description);

        Menu vouchers = new Menu(vouchersTitle, vouchersDescription, R.drawable.ic_gift_voucher, "VOUCHER");
        dashboardMenus.add(vouchers);
//        Menu topup = new Menu(topupTitle, topupDescription, R.drawable.ic_fragment_home_dashboard_menu_item_topup, "TOPUP");
//        dashboardMenus.add(topup);
        Menu electricity = new Menu(fundsTitle, fundsDescription, R.drawable.ic_money, "ELECTRICITY");
        dashboardMenus.add(electricity);
        Menu water = new Menu(transactionsTitle, transactionsDescription, R.drawable.ic_transaction, "WATER");
        dashboardMenus.add(water);

        com.example.fibath.classes.user.User user = new com.example.fibath.classes.user.User();
        user.loadUserSessionData(context);

        if(com.example.fibath.classes.user.User.getUserSessionUserType().equals("5eb1b4d00b65b13950b7bcc8")||com.example.fibath.classes.user.User.getUserSessionUserType().equals("5eb1b13d0b65b13950b7bcc4")) {
            Menu activity = new Menu(downloadsTitle, downloadsDescription, R.drawable.ic_speedometer, "WITHDRAW");
            dashboardMenus.add(activity);
        }


//        SharedPreferences sharedPreferences = context.getSharedPreferences("cookies", MODE_PRIVATE);
//        if (sharedPreferences != null) {
//            if (sharedPreferences.contains("userType")) {
//                String userSessionId = sharedPreferences.getString("userType", "");
//                if (userSessionId == "5eb1b4d00b65b13950b7bcc8") {
//                    Menu activity = new Menu(downloadsTitle, downloadsDescription, R.drawable.ic_speedometer, "WITHDRAW");
//                    dashboardMenus.add(activity);
//                }
//            } else {
//                Toast.makeText(context, "NO SHARED PRE 1", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(context, "NO SHARED PRE", Toast.LENGTH_SHORT).show();
//        }


//        Menu withdraw = new Menu(activityTitle, activityDescription, R.drawable.ic_speedometer, "WITHDRAW");
//        dashboardMenus.add(withdraw);
    }

//    private void showMoreTools() {
//        String trafficPenaltyTitle = "Traffic Penalty";
//        String trafficPenaltyDescription = "Pay Traffic Penalty";
//
//        String busTicketTitle = "Bus";
//        String busTicketDescription = "Buy Bus Tickets";
//
//        String trainTicketTitle = "Train";
//        String trainTicketDescription = "Buy Train Tickets";
//
//        String lotteryTicketTitle = "Lottery";
//        String lotteryTicketDescription = "Buy Lottery Ticket";
//
//        String cinemaTicketTitle = "Cinema";
//        String cinemaTicketDescription = "Buy Cinema Ticket";
//
//        String parksTicketTitle = "Parks";
//        String parksTicketDescription = "Buy Park Ticket";
//
//        if (!isMoreToolsShown) {
//            Menu trafficPenalty = new Menu(trafficPenaltyTitle, trafficPenaltyDescription, R.drawable.ic_fragment_home_dashboard_menu_item_traffic_penality, "TRAFFIC");
//            Menu busTicket = new Menu(busTicketTitle, busTicketDescription, R.drawable.ic_fragment_home_dashboard_menu_item_bus, "BUS");
//            Menu trainTicket = new Menu(trainTicketTitle, trainTicketDescription, R.drawable.ic_fragment_home_dashboard_menu_item_train, "TRAIN");
//            Menu lotteryTicket = new Menu(lotteryTicketTitle, lotteryTicketDescription, R.drawable.ic_fragment_home_dashboard_menu_item_lottery, "LOTTERY");
//            Menu cinemaTicket = new Menu(cinemaTicketTitle, cinemaTicketDescription, R.drawable.ic_fragment_home_dashboard_menu_item_cinema, "CINEMA");
//            Menu parksTicket = new Menu(parksTicketTitle, parksTicketDescription, R.drawable.ic_fragment_home_dashboard_menu_item_parks, "PARKS");
//
//            dashboardMenus.add(busTicket);
//            dashboardMenus.add(trainTicket);
//            dashboardMenus.add(cinemaTicket);
//            dashboardMenus.add(parksTicket);
//            dashboardMenus.add(trafficPenalty);
//            dashboardMenus.add(lotteryTicket);
//            isMoreToolsShown = true;
//        } else {
//            dashboardMenus.remove(11);
//            dashboardMenus.remove(10);
//            dashboardMenus.remove(9);
//            dashboardMenus.remove(8);
//            dashboardMenus.remove(7);
//            dashboardMenus.remove(6);
//
//            isMoreToolsShown = false;
//        }
//        menusContainerAdapter.notifyDataSetChanged();
//        menusContainer.setAdapter(menusContainerAdapter);
//    }

    private void initMainMenuEventHandlers() {

    }
    // endregion
    // endregion

    // class MoreTools extends RecyclerView.ViewHolder {
    //
    //     public MoreTools(@NonNull View itemView) {
    //         super(itemView);
    //         TextView moreItems = view.findViewById(R.id.fragment_home_dashboard_menus_more_tools);
    //         moreItems.setOnClickListener(view -> {
    //             Toast.makeText(itemView.getContext(), "Clicked", Toast.LENGTH_LONG).show();
    //         });
    //     }
    // }
}
