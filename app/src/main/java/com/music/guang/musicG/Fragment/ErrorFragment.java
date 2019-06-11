package com.music.guang.musicG.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.music.guang.musicG.R;
import com.music.guang.musicG.Utilt.NetworkUtils;

public class ErrorFragment
        extends Fragment
{
    public static final String ARG_PAGE = "ARG_PAGE";
    private static ErrorFragment pageFragment;
    private Context context;
    private ConstraintLayout layoutError;
    private View view;

    public static ErrorFragment newInstance(int paramInt)
    {
        Bundle localBundle = new Bundle();
        localBundle.putInt("ARG_PAGE", paramInt);
        if (pageFragment == null) {
            pageFragment = new ErrorFragment();
        }
        pageFragment.setArguments(localBundle);
        return pageFragment;
    }

    public void SetContext(Context paramContext)
    {
        this.context = paramContext;
    }

    public View getView()
    {
        return this.view;
    }

    @Nullable
    public View onCreateView(LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle)
    {
        this.view = paramLayoutInflater.inflate(R.layout.networkerror, paramViewGroup, false);
        this.layoutError = ((ConstraintLayout)this.view.findViewById(R.id.layError));
        this.layoutError.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                if (NetworkUtils.isNetworkAvailable(ErrorFragment.this.context)) {}
            }
        });
        return this.view;
    }
}
