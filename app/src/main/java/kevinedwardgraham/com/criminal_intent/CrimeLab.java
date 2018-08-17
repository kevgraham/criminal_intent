package kevinedwardgraham.com.criminal_intent;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton Crime Datastore
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private Map<UUID, Crime> mCrimes;

    private CrimeLab(Context content) {
        // temp data
        mCrimes = new LinkedHashMap<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            crime.setRequiresPolice(i % 4 == 0);
            mCrimes.put(crime.getId(), crime);
        }
    }

    /**
     * Crimelab Singleton Factory
     * @param context
     * @return
     */
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    /**
     * Get all crimes
     * @return
     */
    public List<Crime> getCrimes() {
        return new ArrayList<>(mCrimes.values());
    }

    /**
     * Get the crime with a specific UUID
     * @param id
     * @return
     */
    public Crime getCrime(UUID id) {
        return mCrimes.get(id);
    }

}
