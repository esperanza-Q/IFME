package com.example.ifme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//카드 여러 장을 옆으로 넘기는 화면(ViewPager2)에 카드 내용을 채워 주는 역할
public class NodeCardAdapter extends RecyclerView.Adapter<NodeCardAdapter.CardViewHolder> {

    ArrayList<NodeCard> cards;
    int selectedIndex = -1; // 선택한 카드 번호 (-1 = 아직 선택 안 함)

    public NodeCardAdapter(ArrayList<NodeCard> cards) {
        this.cards = cards;
    }

    // 카드를 선택하면 호출 → 모든 카드의 상태 칩(선택됨/미선택)을 다시 그림
    public void setSelectedIndex(int index) {
        selectedIndex = index;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_node_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        NodeCard card = cards.get(position);

        holder.tvBranchNumber.setText(String.valueOf(card.branchNumber));
        holder.tvTitle.setText(card.title);
        holder.tvExpect.setText(card.expect);
        holder.tvWorry.setText(card.worry);
        holder.tvGiveUp.setText(card.giveUp);
        holder.tvSatisfaction.setText(card.satisfaction + "/10");

        // 가치 칩 3개: 가치가 있는 만큼만 보이게
        TextView[] valueViews = { holder.tvValue1, holder.tvValue2, holder.tvValue3 };
        for (int i = 0; i < valueViews.length; i++) {
            if (i < card.values.length) {
                valueViews[i].setText(card.values[i]);
                valueViews[i].setVisibility(View.VISIBLE);
            } else {
                valueViews[i].setVisibility(View.GONE);
            }
        }

        // 상태 칩
        if (selectedIndex == -1) {
            holder.tvStatus.setText("선택 전");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_node_status_chip);
        } else if (position == selectedIndex) {
            holder.tvStatus.setText("선택됨");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_node_status_chip);
        } else {
            holder.tvStatus.setText("미선택");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_node_status_chip_off);
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    // 카드 안의 위젯들을 찾아 두는 곳
    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvBranchNumber, tvStatus, tvTitle;
        TextView tvValue1, tvValue2, tvValue3;
        TextView tvExpect, tvWorry, tvGiveUp, tvSatisfaction;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBranchNumber = itemView.findViewById(R.id.tv_branch_number);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvValue1 = itemView.findViewById(R.id.tv_value1);
            tvValue2 = itemView.findViewById(R.id.tv_value2);
            tvValue3 = itemView.findViewById(R.id.tv_value3);
            tvExpect = itemView.findViewById(R.id.tv_expect);
            tvWorry = itemView.findViewById(R.id.tv_worry);
            tvGiveUp = itemView.findViewById(R.id.tv_give_up);
            tvSatisfaction = itemView.findViewById(R.id.tv_satisfaction);
        }
    }
}