package com.vicky.paggingretrofitrecyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vicky.paggingretrofitrecyclerview.adapter.AdapterCallback;
import com.vicky.paggingretrofitrecyclerview.adapter.AdapterClass;
import com.vicky.paggingretrofitrecyclerview.adapter.PaginationScrollListener;
import com.vicky.paggingretrofitrecyclerview.api.ApiInterface;
import com.vicky.paggingretrofitrecyclerview.api.RetrofitClient;
import com.vicky.paggingretrofitrecyclerview.model.ModelClass;
import com.vicky.paggingretrofitrecyclerview.model.ModelResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PaggingRecyclerview";
    AdapterClass adapterClass;
    LinearLayoutManager linearLayoutManager;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView errorText;
    SwipeRefreshLayout refreshLayout;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    private static final int TOTAL_PAGES = 100;
    private int currentPage = PAGE_START;

    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progress_bar);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.retry);
        errorText = findViewById(R.id.error_txt_cause);
        refreshLayout = findViewById(R.id.swipe_layout);

        adapterClass = new AdapterClass(MainActivity.this, new AdapterCallback() {
            @Override
            public void retryLoaded() {
                loadNextPage();
            }
        });

        linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapterClass);

        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage +=1;

                loadNextPage();
            }

            @Override
            public boolean isLoading() {
                return isLastPage;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });

        //init service and load data
        apiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);

        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFirstPage();
            }
        });

        refreshLayout.setOnRefreshListener(this::doRefresh);
    }

    private void loadNextPage() {

        modelResponseCall().enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                adapterClass.removeLoadingFooter();
                isLoading = false;

                List<ModelClass> results = fetchResults(response);
                adapterClass.addAll(results);

                if (currentPage != response.body().getTotalPages()) adapterClass.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doRefresh(){
        progressBar.setVisibility(View.VISIBLE);
        if (modelResponseCall().isExecuted())
            modelResponseCall().cancel();

        adapterClass.getModelClasses().clear();
        adapterClass.notifyDataSetChanged();
        loadFirstPage();

        refreshLayout.setRefreshing(false);
    }

    private void loadFirstPage() {

        hideErrorView();
        currentPage = PAGE_START;

        modelResponseCall().enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                try {
                    if (response.body().getStatus() == 200)
                    {
                        List<ModelClass> results = fetchResults(response);
                        progressBar.setVisibility(View.GONE);
                        adapterClass.addAll(results);

                        if (currentPage <= TOTAL_PAGES) adapterClass.addLoadingFooter();
                        else isLastPage = true;
                    }
                    else if (response.body().getStatus() == 203)
                    {
                        Toast.makeText(MainActivity.this, "No more data!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "something went wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE){
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private List<ModelClass> fetchResults(Response<ModelResponse> response) {
        ModelResponse modelResponse = response.body();

        return modelResponse.getData();

    }

    private Call<ModelResponse> modelResponseCall(){
        return apiInterface.getAllData(
                "UserFeed",
                "Citizen",
                currentPage
        );
    }
}