package com.hosigus.coc_helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.items.Record;

import java.util.List;

/**
 * Created by 某只机智 on 2018/2/27.
 */

public class RecordRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Record> recordList;
    private CallBack mCallBack;

    public RecordRecycleAdapter(List<Record> recordList, CallBack mCallBack) {
        this.recordList = recordList;
        this.mCallBack = mCallBack;
    }

    public void addRecord(Record record) {
        recordList.add(record);
        notifyItemInserted(recordList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Record.TYPE_NOTICE) {
            return new NoticeHolder(inflater.inflate(R.layout.item_game_notice,parent,false));
        } else if (viewType == Record.TYPE_STORY) {
            return new StoryHolder(inflater.inflate(R.layout.item_game_record,parent,false));
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return recordList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (viewType == Record.TYPE_NOTICE) {
            ((NoticeHolder)holder).noticeV.setText(recordList.get(position).getDetail());
        } else if (viewType == Record.TYPE_STORY) {
            StoryHolder storyH = (StoryHolder) holder;
            Record record = recordList.get(position);
            storyH.titleV.setText(record.getTitle());
            storyH.detailV.setText(record.getDetail());
            storyH.showBtn.setOnClickListener(v->mCallBack.onShowRecord(record));
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class StoryHolder extends RecyclerView.ViewHolder {
        TextView titleV,detailV;
        Button showBtn;
        StoryHolder(View itemView) {
            super(itemView);
            titleV = itemView.findViewById(R.id.tv_item_record_title);
            detailV = itemView.findViewById(R.id.tv_item_record_detail);
            showBtn = itemView.findViewById(R.id.btn_item_record_show);
        }
    }

    class NoticeHolder extends RecyclerView.ViewHolder{
        TextView noticeV;
        NoticeHolder(View itemView) {
            super(itemView);
            noticeV = itemView.findViewById(R.id.tv_item_record_notice);
        }
    }

    public interface CallBack{
        void onShowRecord(Record record);
    }
}
