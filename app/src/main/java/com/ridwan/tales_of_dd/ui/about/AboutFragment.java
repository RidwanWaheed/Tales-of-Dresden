package com.ridwan.tales_of_dd.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ridwan.tales_of_dd.R;
import com.ridwan.tales_of_dd.ui.team.TeamMember;
import com.ridwan.tales_of_dd.ui.team.TeamMemberAdapter;
import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {
    // UI Components
    private RecyclerView teamMembersRecycler;
    private TeamMemberAdapter adapter;
    private View rootView;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);

        initializeViews();
        setupRecyclerView();

        return rootView;
    }

    private void initializeViews() {
        teamMembersRecycler = rootView.findViewById(R.id.team_recycler_view);
    }

    private void setupRecyclerView() {
        if (!isAdded()) return;

        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(new TeamMember("Waheed Ridwan", "url_to_waheed_image"));
        teamMembers.add(new TeamMember("Marcel Siedlich", "url_to_marcel_image"));
        teamMembers.add(new TeamMember("Keying Fan", "url_to_keying_image"));

        adapter = new TeamMemberAdapter(teamMembers);
        teamMembersRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        teamMembersRecycler.setAdapter(adapter);
    }
}