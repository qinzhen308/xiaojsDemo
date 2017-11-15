package cn.xiaojs.xma.ui.classroom2.base;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.exoplayer.EventLogger;
import cn.xiaojs.xma.common.exoplayer.ExoUtil;


/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlayerFragment extends MovieFragment implements Player.EventListener,
        PlaybackControlView.VisibilityListener {

    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";
    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";

    public static final String URI_LIST_EXTRA = "uris";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";
    public static final String AUTO_PLAY = "aplay";
    public static final String EXTRA_OBJECT = "object";


    @BindView(R.id.debug_text_view)
    TextView debugTextView;
    @BindView(R.id.progress_view)
    ProgressBar loadingView;
    @BindView(R.id.player_view)
    SimpleExoPlayerView playerView;


    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private TrackGroupArray lastSeenTrackGroupArray;
    private EventLogger eventLogger;
    private DebugTextViewHelper debugViewHelper;

    private Handler mainHandler;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;

    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader
    private Uri loadedAdTagUri;
    private ViewGroup adOverlayViewGroup;
    private boolean inErrorState;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_exoplayer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        shouldAutoPlay = getArguments().getBoolean(AUTO_PLAY, true);
        mainHandler = new Handler();
        clearResumePosition();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        playerView.setControllerVisibilityListener(this);
        playerView.requestFocus();

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
        }
    }


    // Activity input
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        //return super.dispatchKeyEvent(event) || playerView.dispatchKeyEvent(event);
        return playerView.dispatchKeyEvent(event);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAdsLoader();
    }

    @Override
    public void onRotate(int orientation) {
        //do nothing
        onRotateToInitBoard(orientation);
    }

    @Override
    public void closeMovie() {
        //Do nothing
    }

    @Override
    public void onTopbackClick(View view, boolean land) {

    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        // Do nothing.
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        updateButtonVisibilities();
        if (trackGroups != lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    showToast(R.string.error_unsupported_video);
                }
                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    showToast(R.string.error_unsupported_audio);
                }
            }
            lastSeenTrackGroupArray = trackGroups;
        }

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_ENDED:
                //hide loading
                showOrHiddenLoading(View.GONE);
                showControls();
                break;
            case Player.STATE_BUFFERING:
                //show loading
                //showOrHiddenLoading(View.VISIBLE);
                break;
            case Player.STATE_IDLE:
            case Player.STATE_READY:
                //hide loading
                showOrHiddenLoading(View.GONE);
                break;
        }

        updateButtonVisibilities();
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

        // Do nothing.
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        String errorString = null;
        if (error.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = error.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause()
                            instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        inErrorState = true;
        if (isBehindLiveWindow(error)) {
            clearResumePosition();
            initializePlayer();
        } else {
            updateResumePosition();
            updateButtonVisibilities();
            showControls();
        }

        //hide loading
        showOrHiddenLoading(View.GONE);
    }

    @Override
    public void onPositionDiscontinuity() {

        if (inErrorState) {
            // This will only occur if the user has performed a seek whilst in the error state. Update the
            // resume position so that if the user then retries, playback will resume from the position to
            // which they seeked.
            updateResumePosition();
        }
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        // Do nothing.
    }

//    public void onNewIntent(Intent intent) {
//        releasePlayer();
//        shouldAutoPlay = true;
//        clearResumePosition();
//        setIntent(intent);
//    }

    public Bitmap getBitmap() {
        View view = playerView.getVideoSurfaceView();
        if (view instanceof TextureView) {
            TextureView textureView = (TextureView) view;
            return textureView.getBitmap();
        }

        return null;
    }


    private void initializePlayer() {

        Bundle arguments = getArguments();

        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            lastSeenTrackGroupArray = null;
            eventLogger = new EventLogger(trackSelector);


            String uuidStr = arguments.getString(DRM_SCHEME_UUID_EXTRA, null);

            UUID drmSchemeUuid = uuidStr != null ? UUID.fromString(uuidStr) : null;
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            if (drmSchemeUuid != null) {
//                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
//                String[] keyRequestPropertiesArray =
//                        intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);

                String drmLicenseUrl = arguments.getString(DRM_LICENSE_URL);
                String[] keyRequestPropertiesArray =
                        arguments.getStringArray(DRM_KEY_REQUEST_PROPERTIES);


                int errorStringId = R.string.error_drm_unknown;
                if (Util.SDK_INT < 18) {
                    errorStringId = R.string.error_drm_not_supported;
                } else {
                    try {
                        drmSessionManager = buildDrmSessionManagerV18(drmSchemeUuid, drmLicenseUrl,
                                keyRequestPropertiesArray);
                    } catch (UnsupportedDrmException e) {
                        errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                                ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                    }
                }
                if (drmSessionManager == null) {
                    showToast(errorStringId);
                    return;
                }
            }

            boolean preferExtensionDecoders = arguments.getBoolean(PREFER_EXTENSION_DECODERS, false);
            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                    ExoUtil.useExtensionRenderers()
                            ? (preferExtensionDecoders
                            ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext(),
                    drmSessionManager, extensionRendererMode);

            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
            player.addListener(this);
            player.addListener(eventLogger);
            player.addMetadataOutput(eventLogger);
            player.setAudioDebugListener(eventLogger);
            player.setVideoDebugListener(eventLogger);

            playerView.setPlayer(player);
            player.setPlayWhenReady(shouldAutoPlay);
            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            debugViewHelper.start();

        }

        Uri[] uris;
        String[] extensions;
        String[] uriStrings = arguments.getStringArray(URI_LIST_EXTRA);
        if (uriStrings != null && uriStrings.length > 0) {
            uris = new Uri[uriStrings.length];
            for (int i = 0; i < uriStrings.length; i++) {
                uris[i] = Uri.parse(uriStrings[i]);
            }
            extensions = arguments.getStringArray(EXTENSION_LIST_EXTRA);
            if (extensions == null) {
                extensions = new String[uriStrings.length];
            }
        } else {
            showToast(getString(R.string.empty_uri));
            return;
        }

        if (Util.maybeRequestReadExternalStoragePermission(getActivity(), uris)) {
            // The player will be reinitialized if the permission is granted.
            return;
        }
        MediaSource[] mediaSources = new MediaSource[uris.length];
        for (int i = 0; i < uris.length; i++) {
            mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        String adTagUriString = arguments.getString(AD_TAG_URI_EXTRA, null);
        if (adTagUriString != null) {
            Uri adTagUri = Uri.parse(adTagUriString);
            if (!adTagUri.equals(loadedAdTagUri)) {
                releaseAdsLoader();
                loadedAdTagUri = adTagUri;
            }
            try {
                mediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));
            } catch (Exception e) {
                showToast(R.string.ima_not_loaded);
            }
        } else {
            releaseAdsLoader();
        }
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        player.prepare(mediaSource, !haveResumePosition, false);
        inErrorState = false;
        updateButtonVisibilities();
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(UUID uuid,
                                                                              String licenseUrl,
                                                                              String[] keyRequestPropertiesArray)
            throws UnsupportedDrmException {
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
                buildHttpDataSourceFactory(false));
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        return new DefaultDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback,
                null, mainHandler, eventLogger);
    }


    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }


    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ExoUtil.buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ExoUtil.buildDataSourceFactory(getContext(), useBandwidthMeter ? BANDWIDTH_METER : null);
    }


    /**
     * Returns an ads media source, reusing the ads loader if one exists.
     *
     * @throws Exception Thrown if it was not possible to create an ads media source, for example,
     *                   due to a missing dependency.
     */
    private MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) throws Exception {
        // Load the extension source using reflection so the demo app doesn't have to depend on it.
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
        Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
        if (imaAdsLoader == null) {
            imaAdsLoader = loaderClass.getConstructor(Context.class, Uri.class)
                    .newInstance(this, adTagUri);
            adOverlayViewGroup = new FrameLayout(getContext());
            // The demo app has a non-null overlay frame layout.
            playerView.getOverlayFrameLayout().addView(adOverlayViewGroup);
        }
        Class<?> sourceClass =
                Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsMediaSource");
        Constructor<?> constructor = sourceClass.getConstructor(MediaSource.class,
                DataSource.Factory.class, loaderClass, ViewGroup.class);
        return (MediaSource) constructor.newInstance(mediaSource, mediaDataSourceFactory, imaAdsLoader,
                adOverlayViewGroup);
    }


    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = Math.max(0, player.getContentPosition());
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    private void releasePlayer() {
        if (player != null) {
            debugViewHelper.stop();
            debugViewHelper = null;
            shouldAutoPlay = player.getPlayWhenReady();
            updateResumePosition();
            player.release();
            player = null;
            trackSelector = null;
            eventLogger = null;
        }
    }

    private void releaseAdsLoader() {
        if (imaAdsLoader != null) {
            try {
                Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");
                Method releaseMethod = loaderClass.getMethod("release");
                releaseMethod.invoke(imaAdsLoader);
            } catch (Exception e) {
                // Should never happen.
                throw new IllegalStateException(e);
            }
            imaAdsLoader = null;
            loadedAdTagUri = null;
            playerView.getOverlayFrameLayout().removeAllViews();
        }
    }

    // User controls

    private void updateButtonVisibilities() {
//        debugRootView.removeAllViews();
//
//        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);
//        debugRootView.addView(retryButton);
//
//        if (player == null) {
//            return;
//        }
//
//        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
//        if (mappedTrackInfo == null) {
//            return;
//        }
//
//        for (int i = 0; i < mappedTrackInfo.length; i++) {
//            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
//            if (trackGroups.length != 0) {
//                Button button = new Button(getContext());
//                int label;
//                switch (player.getRendererType(i)) {
//                    case C.TRACK_TYPE_AUDIO:
//                        label = R.string.audio;
//                        break;
//                    case C.TRACK_TYPE_VIDEO:
//                        label = R.string.video;
//                        break;
//                    case C.TRACK_TYPE_TEXT:
//                        label = R.string.text;
//                        break;
//                    default:
//                        continue;
//                }
//                button.setText(label);
//                button.setTag(i);
//                button.setOnClickListener(this);
//                debugRootView.addView(button, debugRootView.getChildCount() - 1);
//            }
//        }
    }


    private void showControls() {
        //debugTextView.setVisibility(View.VISIBLE);
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showOrHiddenLoading(int visible) {
        loadingView.setVisibility(visible);
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }


    @Override
    public void onClosed() {

    }

    @Override
    public void onOpened() {

    }
}
