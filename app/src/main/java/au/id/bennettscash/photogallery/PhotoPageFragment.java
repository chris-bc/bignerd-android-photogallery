package au.id.bennettscash.photogallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by chris on 3/09/15.
 */
public class PhotoPageFragment extends VisibleFragment {
    private String url;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        url = getActivity().getIntent().getData().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, parent, false);

        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        final TextView titleTextView = (TextView)v.findViewById(R.id.titleTextView);

        webView = (WebView)v.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progress);
                }
            }

            public void onReceivedTitle(WebView webView, String title) {
                titleTextView.setText(title);
            }
        });

        webView.loadUrl(url);

        return v;
    }
}
