/**
 * Copyright (C) 2020 The LineageOS Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.settings.thermal;

import android.annotation.Nullable;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.settingslib.applications.ApplicationsState;

import org.lineageos.settings.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ThermalSettingsFragment extends PreferenceFragment
        implements ApplicationsState.Callbacks {

    private AllPackagesAdapter mAllPackagesAdapter;
    private ApplicationsState mApplicationsState;
    private ApplicationsState.Session mSession;
    private ActivityFilter mActivityFilter;
    private Map<String, ApplicationsState.AppEntry> mEntryMap =
            new HashMap<String, ApplicationsState.AppEntry>();

    private ThermalUtils mThermalUtils;
    private RecyclerView mAppsRecyclerView;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
        mSession = mApplicationsState.newSession(this);
        mSession.onResume();
        mActivityFilter = new ActivityFilter(getActivity().getPackageManager());

        mAllPackagesAdapter = new AllPackagesAdapter();

        mThermalUtils = new ThermalUtils(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.thermal_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAppsRecyclerView = view.findViewById(R.id.thermal_rv_view);
        mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAppsRecyclerView.setAdapter(mAllPackagesAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getResources().getString(R.string.thermal_title));
        rebuild();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSession.onPause();
        mSession.onDestroy();
    }

    @Override
    public void onPackageListChanged() {
        mActivityFilter.updateLauncherInfoList();
        rebuild();
    }

    @Override
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> entries) {
        if (entries != null) {
            handleAppEntries(entries);
        }
    }

    @Override
    public void onLoadEntriesCompleted() {
        rebuild();
    }

    @Override
    public void onAllSizesComputed() {
    }

    @Override
    public void onLauncherInfoChanged() {
    }

    @Override
    public void onPackageIconChanged() {
    }

    @Override
    public void onPackageSizeChanged(String packageName) {
    }

    @Override
    public void onRunningStateChanged(boolean running) {
    }

    /** Called by the activity's SearchView to filter the app list. */
    public void setSearchQuery(String query) {
        mAllPackagesAdapter.setQuery(query);
    }

    private void handleAppEntries(List<ApplicationsState.AppEntry> entries) {
        mEntryMap.clear();
        for (ApplicationsState.AppEntry e : entries) {
            mEntryMap.put(e.info.packageName, e);
        }
        mAllPackagesAdapter.setEntries(entries);
    }

    private void rebuild() {
        mSession.rebuild(mActivityFilter, ApplicationsState.ALPHA_COMPARATOR);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView profile;
        private ImageView icon;
        private View rootView;

        private ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.app_name);
            this.profile = view.findViewById(R.id.app_profile);
            this.icon = view.findViewById(R.id.app_icon);
            this.rootView = view;

            view.setTag(this);
        }
    }

    private class AllPackagesAdapter extends RecyclerView.Adapter<ViewHolder>
            implements SectionIndexer {

        private List<ApplicationsState.AppEntry> mAllEntries = new ArrayList<>();
        private List<ApplicationsState.AppEntry> mEntries = new ArrayList<>();
        private String[] mSections;
        private int[] mPositions;
        private String mQuery = "";

        @Override
        public int getItemCount() {
            return mEntries.size();
        }

        @Override
        public long getItemId(int position) {
            return mEntries.get(position).id;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.thermal_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ApplicationsState.AppEntry entry = mEntries.get(position);

            if (entry == null) {
                return;
            }

            holder.title.setText(entry.label);
            mApplicationsState.ensureIcon(entry);
            holder.icon.setImageDrawable(entry.icon);
            int packageState = mThermalUtils.getStateForPackage(entry.info.packageName);
            holder.profile.setText(ThermalUtils.getProfileNameRes(packageState));

            final int count = getItemCount();
            if (count == 1) {
                holder.rootView.setBackgroundResource(R.drawable.settingslib_round_background);
            } else if (position == 0) {
                holder.rootView.setBackgroundResource(R.drawable.settingslib_round_background_top);
            } else if (position == count - 1) {
                holder.rootView.setBackgroundResource(
                        R.drawable.settingslib_round_background_bottom);
            } else {
                holder.rootView.setBackgroundResource(
                        R.drawable.settingslib_round_background_center);
            }

            holder.rootView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ThermalProfileActivity.class);
                intent.putExtra(ThermalProfileFragment.EXTRA_PACKAGE_NAME,
                        entry.info.packageName);
                intent.putExtra(ThermalProfileFragment.EXTRA_APP_LABEL, entry.label);
                startActivity(intent);
            });
        }

        private void setEntries(List<ApplicationsState.AppEntry> entries) {
            mAllEntries = entries;
            applyFilter();
        }

        private void setQuery(String query) {
            mQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
            applyFilter();
        }

        private void applyFilter() {
            final ArrayList<ApplicationsState.AppEntry> filtered = new ArrayList<>();
            for (ApplicationsState.AppEntry entry : mAllEntries) {
                if (TextUtils.isEmpty(mQuery) || (entry.label != null
                        && entry.label.toLowerCase(Locale.ROOT).contains(mQuery))) {
                    filtered.add(entry);
                }
            }
            mEntries = filtered;
            computeSections(filtered);
            notifyDataSetChanged();
        }

        private void computeSections(List<ApplicationsState.AppEntry> entries) {
            final ArrayList<String> sections = new ArrayList<String>();
            final ArrayList<Integer> positions = new ArrayList<Integer>();
            final PackageManager pm = getActivity().getPackageManager();
            String lastSectionIndex = null;

            for (int i = 0; i < entries.size(); i++) {
                final ApplicationInfo info = entries.get(i).info;
                final String label = (String) info.loadLabel(pm);
                final String sectionIndex;

                if (!info.enabled) {
                    sectionIndex = "--"; // XXX
                } else if (TextUtils.isEmpty(label)) {
                    sectionIndex = "";
                } else {
                    sectionIndex = label.substring(0, 1).toUpperCase();
                }

                if (lastSectionIndex == null ||
                        !TextUtils.equals(sectionIndex, lastSectionIndex)) {
                    sections.add(sectionIndex);
                    positions.add(i);
                    lastSectionIndex = sectionIndex;
                }
            }

            mSections = sections.toArray(new String[sections.size()]);
            mPositions = new int[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                mPositions[i] = positions.get(i);
            }
        }

        @Override
        public int getPositionForSection(int section) {
            if (mPositions == null || section < 0 || section >= mSections.length) {
                return -1;
            }

            return mPositions[section];
        }

        @Override
        public int getSectionForPosition(int position) {
            if (mPositions == null || position < 0 || position >= getItemCount()) {
                return -1;
            }

            final int index = Arrays.binarySearch(mPositions, position);

            /*
             * Consider this example: section positions are 0, 3, 5; the supplied
             * position is 4. The section corresponding to position 4 starts at
             * position 3, so the expected return value is 1. Binary search will not
             * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
             * To get from that number to the expected value of 1 we need to negate
             * and subtract 2.
             */
            return index >= 0 ? index : -index - 2;
        }

        @Override
        public Object[] getSections() {
            return mSections;
        }
    }

    private class ActivityFilter implements ApplicationsState.AppFilter {

        private final PackageManager mPackageManager;
        private final List<String> mLauncherResolveInfoList = new ArrayList<String>();

        private ActivityFilter(PackageManager packageManager) {
            this.mPackageManager = packageManager;

            updateLauncherInfoList();
        }

        public void updateLauncherInfoList() {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(i, 0);

            synchronized (mLauncherResolveInfoList) {
                mLauncherResolveInfoList.clear();
                for (ResolveInfo ri : resolveInfoList) {
                    mLauncherResolveInfoList.add(ri.activityInfo.packageName);
                }
            }
        }

        @Override
        public void init() {
        }

        @Override
        public boolean filterApp(ApplicationsState.AppEntry entry) {
            boolean show = !mAllPackagesAdapter.mEntries.contains(entry.info.packageName);
            if (show) {
                synchronized (mLauncherResolveInfoList) {
                    show = mLauncherResolveInfoList.contains(entry.info.packageName);
                }
            }
            return show;
        }
    }
}
