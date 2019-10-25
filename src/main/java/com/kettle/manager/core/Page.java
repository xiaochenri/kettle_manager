package com.kettle.manager.core;

public class Page {

    private int totalCount;//总数

    private int totalPage;//总页数

    private int currentPageIndex;//当前第几页

    private Page(Builder builder) {
        setTotalCount(builder.totalCount);
        setTotalPage(builder.totalPage);
        setCurrentPageIndex(builder.currentPageIndex);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Page copy) {
        Builder builder = new Builder();
        builder.totalCount = copy.getTotalCount();
        builder.totalPage = copy.getTotalPage();
        builder.currentPageIndex = copy.getCurrentPageIndex();
        return builder;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public static final class Builder {
        private int totalCount;
        private int totalPage;
        private int currentPageIndex;

        private Builder() {
        }

        public Builder totalCount(int val) {
            totalCount = val;
            return this;
        }

        public Builder totalPage(int val) {
            totalPage = val;
            return this;
        }

        public Builder currentPageIndex(int val) {
            currentPageIndex = val;
            return this;
        }

        public Page build() {
            return new Page(this);
        }
    }
}
