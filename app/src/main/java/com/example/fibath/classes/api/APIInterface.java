package com.example.fibath.classes.api;


import com.example.fibath.classes.model.AccountResponse;
import com.example.fibath.classes.model.AgentUser;
import com.example.fibath.classes.model.BulkVoucherResponse;
import com.example.fibath.classes.model.FundHistoryModel;
import com.example.fibath.classes.model.FundRequestListResponse;
import com.example.fibath.classes.model.FundRequestResponse;
import com.example.fibath.classes.model.FundResponse;
import com.example.fibath.classes.model.LoginResponse;
import com.example.fibath.classes.model.LogoutResponse;

import com.example.fibath.classes.model.MoneyTransferResponse;
import com.example.fibath.classes.model.SalesPerFaceValueModel;
import com.example.fibath.classes.model.SyncResponse;
import com.example.fibath.classes.model.PasswordResponse;
import com.example.fibath.classes.model.SoldVoucherResponse;
import com.example.fibath.classes.model.TransactionReportResponse;
import com.example.fibath.classes.model.User;
import com.example.fibath.classes.model.VoucherPurchaseResponse;
import com.example.fibath.classes.model.VoucherTransactionPagination;
import com.example.fibath.classes.model.WeeklySalesReportResponse;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    public static final String BASE_URL = "http://localhost:5000";
    public static final String APP_VERSION = "1.0";
    // Our Part Begins Here

    @POST("/api/users/login/retailer")
    Observable<Response<LoginResponse>> retailerLogin(@Header("application_version") String application_version, @Body HashMap<String, String> map);

    @GET("/api/users/retailer")
    Observable<Response<User>> retailerInfo(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @POST("/api/voucher/buy")
    Observable<Response<ArrayList<VoucherPurchaseResponse>>> retailerBuyVouchers(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, Integer> map, @Query("downloaded") String download, @Query("airtime") String airtime);

    @POST("/api/voucher/download")
    Observable<Response<ArrayList<SoldVoucherResponse>>> retailerDownloadVouchers(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @POST("/api/voucher/download/sync")
    Observable<Response<SyncResponse>> retailerSyncDownloadedVouchers(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body JsonObject jsonObject);

    @POST("/api/transaction/retailer")
    Observable<Response<VoucherTransactionPagination>> retailerGetVoucherTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, Integer> map);

    @POST("/api/transaction/retailer/transactionWithSerial")
    Observable<Response<VoucherTransactionPagination>> retailerGetVoucherTransactionWithMinAndMAxSerial(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, Integer> map);


    @GET("/api/voucher/get_bulk_voucher")
    Observable<Response<ArrayList<BulkVoucherResponse>>> retailerGetBulkVoucherTransaction(@Header("x-auth-token") String token);

    @POST("/api/voucher/voucher/filter")
    Observable<Response<ArrayList<BulkVoucherResponse>>> retailerGetVoucherFilter(@Header("x-auth-token") String token, @Body HashMap<String, Object> map);


    @POST("/api/transaction/retailer/each")
    Observable<Response<ArrayList<BulkVoucherResponse>>> retailerGetEachOfBulkVoucherTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> map);

    @GET("/api/account/")
    Observable<Response<AccountResponse>> retailerAccountInformation(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @GET("/api/transaction/")
    Observable<Response<ArrayList<FundResponse>>> retailerGetFundTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @GET("/api/transaction/child/agent/sent")
    Observable<Response<ArrayList<FundResponse>>> retailerGetSentFundTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @GET("/api/transaction/child/agent/received")
    Observable<Response<ArrayList<FundResponse>>> retailerGetReceivedFundTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @POST("/api/transaction/child/agent")
    Observable<Response<ArrayList<FundResponse>>> getChildUserTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> map);

    @POST("/api/users/logout")
    Observable<Response<LogoutResponse>> retailerLogout(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @POST("/api/users/change_password")
    Observable<Response<PasswordResponse>> changePassword(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> map);

    @POST("/api/users/agent/register")
    Observable<Response<AgentUser>> registerAgent(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> map);

    @GET("api/users/child/users")
    Observable<Response<ArrayList<AgentUser>>> getChildAgents(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @POST("api/users/search?")
    Observable<Response<ArrayList<User>>> searchUsers(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Query("searchText") String searchText);

    @POST("api/account/credit")
    Observable<Response<MoneyTransferResponse>> transferMoney(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> map);

    @POST("api/account/credit/agent")
    Observable<Response<MoneyTransferResponse>> transferMoneyWithCommission(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, Object> map);

    @POST("api/users/agent/profile")
    Observable<Response<AgentUser>> getUserProfile(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> map);

    @POST("api/transaction/mobile/money/sent?today=true")
    Observable<Response<TransactionReportResponse>> getTotalBalanceSentToday(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body);

    @POST("api/transaction/mobile/money/received?today=true")
    Observable<Response<TransactionReportResponse>> getTotalBalanceReceivedToday(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body);

    @POST("api/account/refund/money")
    Observable<Response<FundResponse>> revertTransaction(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, Object> body);

    @POST("api/account/refund/direct/money")
    Observable<Response<FundResponse>> deductUserBalance(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, Object> body);

    @POST("api/users/change/users/profile")
    Observable<Response<AgentUser>> updateAgent(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body);

    @POST("api/fund/request")
    Observable<Response<FundRequestResponse>> fundRequest(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body);

    @GET("api/fund/requests/sent")
    Observable<Response<ArrayList<FundRequestListResponse>>> getSentFundRequests(@Header("x-auth-token") String token, @Header("application_version") String application_version);

    @POST("api/report/mobile/sales/total")
    Observable<Response<WeeklySalesReportResponse>> getFilteredSalesReport(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body, @Query("filter") String filterEnabled, @Query("searchFilter") String searchText);

    @POST("api/report/mobile/fund/history")
    Observable<Response<FundHistoryModel>> getFundTransactionHistory(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body, @Query("filter") String filterEnabled, @Query("searchFilter") String searchText);

    @POST("api/report/mobile/get/each/facevalue")
    Observable<Response<SalesPerFaceValueModel>> getSalesPerFaceValue(@Header("x-auth-token") String token, @Header("application_version") String application_version, @Body HashMap<String, String> body, @Query("filter") String filterEnabled, @Query("searchFilter") String searchText);
}
