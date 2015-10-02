package com.matthiasko.popularmovies2;

import java.util.ArrayList;

/**
 * Created by matthiasko on 9/29/15.
 */
public interface FetchExtrasResponse {
    void onSuccess(ArrayList<YTObject> result);
}
