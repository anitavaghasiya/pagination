package com.vicky.paggingretrofitrecyclerview.model;

import java.util.List;

public class ModelResponse {

    private Integer status, PageNumber, TotalPages, TotalRecords, PageSize;
    private String status_message;
    private List<ModelClass> data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPageNumber() {
        return PageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        PageNumber = pageNumber;
    }

    public Integer getTotalPages() {
        return TotalPages;
    }

    public void setTotalPages(Integer totalPages) {
        TotalPages = totalPages;
    }

    public Integer getTotalRecords() {
        return TotalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        TotalRecords = totalRecords;
    }

    public Integer getPageSize() {
        return PageSize;
    }

    public void setPageSize(Integer pageSize) {
        PageSize = pageSize;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public List<ModelClass> getData() {
        return data;
    }

    public void setData(List<ModelClass> data) {
        this.data = data;
    }
}
