package kevinedwardgraham.com.criminal_intent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {


    private static final int NORMAL_CRIME = 0;
    private static final int SEVERE_CRIME = 1;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private int mLastUpdatePosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        // get model
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            // pass model to adapter
            mAdapter = new CrimeAdapter(crimes);
            // pass adapter to recycler view
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            if (mLastUpdatePosition > -1) {
                // update specific item in recycler view
                mAdapter.notifyItemChanged(mLastUpdatePosition);
                mLastUpdatePosition = -1;
            } else {
                // update all items in recycler view
                mAdapter.notifyDataSetChanged();
            }
        }

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
