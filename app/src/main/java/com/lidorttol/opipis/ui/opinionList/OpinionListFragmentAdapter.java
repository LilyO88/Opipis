package com.lidorttol.opipis.ui.opinionList;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.lidorttol.opipis.R;
import com.lidorttol.opipis.data.Opinion;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OpinionListFragmentAdapter extends ListAdapter<Opinion, OpinionListFragmentAdapter.ViewHolder> {

    public OpinionListFragmentAdapter() {
        super(new DiffUtil.ItemCallback<Opinion>() {
            @Override
            public boolean areItemsTheSame(@NonNull Opinion oldItem, @NonNull Opinion newItem) {
                return TextUtils.equals(oldItem.getId_opinion(), newItem.getId_opinion());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Opinion oldItem, @NonNull Opinion newItem) {
                return TextUtils.equals(oldItem.getId_banio(), newItem.getId_banio())
                        && TextUtils.equals(oldItem.getUsuario(), newItem.getUsuario())
                        && TextUtils.equals(oldItem.getComentario(), newItem.getComentario())
                        && oldItem.getFecha().equals(newItem.getFecha())
                        && oldItem.getGlobal() == newItem.getGlobal()
                        && oldItem.getLimpieza() == newItem.getLimpieza()
                        && oldItem.getTamanio() == newItem.getTamanio()
                        && oldItem.isPestillo() == newItem.isPestillo()
                        && oldItem.isPapel() == newItem.isPapel()
                        && oldItem.isMinusvalido() == newItem.isMinusvalido()
                        && oldItem.isUnisex() == newItem.isUnisex();
            }
        });
    }

    @NonNull
    @Override
    public OpinionListFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_opinion_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(getItem(position).getId_opinion());
    }

    @Override
    public Opinion getItem(int position) {
        return super.getItem(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView lblUsuario;
        private final TextView lblDate;
        private final TextView lblComment;
        private final RatingBar ratGlobal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblUsuario= ViewCompat.requireViewById(itemView, R.id.cv_lblUsuario);
            lblDate = ViewCompat.requireViewById(itemView, R.id.cv_lblDate);
            lblComment = ViewCompat.requireViewById(itemView, R.id.cv_lblComentario);
            ratGlobal = ViewCompat.requireViewById(itemView, R.id.cv_ratBanio);
        }

        private String getDate(Opinion opinion) {
            Date date = opinion.getFecha();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            return formatter.format(date);
        }

        public void bind(Opinion opinion) {
            lblUsuario.setText(opinion.getUsuario());
            lblDate.setText(getDate(opinion));
            lblComment.setText(opinion.getComentario());
            ratGlobal.setRating((float) opinion.getGlobal());
        }
    }
}