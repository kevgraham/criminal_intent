package kevinedwardgraham.com.criminal_intent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CrimeListFragment extends Fragment {


    private static final int NORMAL_CRIME = 0;
    private static final int SEVERE_CRIME = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private LinearLayout mNoCrimesLayout;
    private Button mNewCrimeButton;

    private int mLastUpdatePosition = -1;
    private boolean mSubtitleVisible;

    private List<Crime> mCrimeList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNoCrimesLayout = view.findViewById(R.id.linearlayout_no_crimes);

        mNewCrimeButton = view.findViewById(R.id.button_new_crime);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
            }
        });


        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        // get model
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        if (mAdapter == null) {
            // pass model to adapter
            mAdapter = new CrimeAdapter(mCrimeList);
            // pass adapter to recycler view
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            // update all items in recycler view
            mCrimeList.clear();
            mCrimeList.addAll(crimeLab.getCrimes());
            mAdapter.notifyDataSetChanged();
        }

        if (mCrimeList != null && mCrimeList.size() > 0) {
            mNoCrimesLayout.setVisibility(View.INVISIBLE);
        } else {
            mNoCrimesLayout.setVisibility(View.VISIBLE);
        }

        updateSubtitle();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                // create crime
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);

                // go to detail page
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);

                return true;
            case R.id.show_subtitle:
                // toggle subtitle item
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);


        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private abstract class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected Crime mCrime;

        public CrimeHolder(LayoutInflater inflator, ViewGroup container, @LayoutRes int resource) {
            super(inflator.inflate(resource, container, false));
        }

        public abstract void bind(Crime crime);

        @Override
        public void onClick(View v) {
            mLastUpdatePosition = getAdapterPosition();
            startActivity(CrimePagerActivity.newIntent(getActivity(), mCrime.getId()));
        }
    }

    /**
     * Binds to a list_item_crime
     */
    private class NormalCrimeHolder extends CrimeHolder {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mCrimeSolvedImageView;

        public NormalCrimeHolder(LayoutInflater inflator, ViewGroup container) {
            super(inflator, container, R.layout.list_item_crime);
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mCrimeSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDateString());
            mCrimeSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * Binds to a list_item_severe
     */
    private class SevereCrimeHolder extends CrimeHolder {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mPoliceButton;

        public SevereCrimeHolder(LayoutInflater inflator, ViewGroup container) {
            super(inflator, container, R.layout.list_item_severe);
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mPoliceButton = itemView.findViewById(R.id.button_police);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDateString());
        }
    }

    /**
     * Connects Crimes from CrimeLab to CrimeHolder
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            switch (viewType) {
                case SEVERE_CRIME:
                    return new SevereCrimeHolder(layoutInflater, parent);
                case NORMAL_CRIME:
                    return new NormalCrimeHolder(layoutInflater, parent);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            return crime.isRequiresPolice() ? SEVERE_CRIME : NORMAL_CRIME;
        }
    }
}
