package com.ridwan.tales_of_dd.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ridwan.tales_of_dd.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder> {
    private List<TeamMember> teamMembers;

    public TeamMemberAdapter(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
    }

    @NonNull
    @Override
    public TeamMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_member, parent, false);
        return new TeamMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMemberViewHolder holder, int position) {
        holder.bind(teamMembers.get(position));
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    static class TeamMemberViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView profileImage;
        private final TextView nameText;

        public TeamMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            nameText = itemView.findViewById(R.id.name_text);
        }

        public void bind(TeamMember member) {
            nameText.setText(member.getName());
            Glide.with(itemView.getContext())
                    .load(member.getImageUrl())
                    .centerCrop()
                    .into(profileImage);
        }
    }
}
