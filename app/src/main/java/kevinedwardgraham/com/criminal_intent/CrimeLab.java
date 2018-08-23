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
        mCrimes = new LinkedHashMap<>();
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
     * Adds the given Crime
     * @param crime
     */
    public void addCrime(Crime crime) {
        mCrimes.put(crime.getId(), crime);
    }

    /**
     * Removes the given Crime
     * @param crime
     */
    public void removeCrime(Crime crime) {
        if (getCrime(crime.getId()) != null) {
            mCrimes.remove(crime.getId());
        }
    }

    /**
     * Gets all Crimes
     * @return
     */
    public List<Crime> getCrimes() {
        return new ArrayList<>(mCrimes.values());
    }

    /**
     * Gets the Crime with a specific UUID
     * @param id
     * @return
     */
    public Crime getCrime(UUID id) {
        return mCrimes.get(id);
    }

}
