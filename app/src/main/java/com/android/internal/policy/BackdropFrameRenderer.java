package com.android.internal.policy;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.DisplayListCanvas;
import android.view.RenderNode;
import android.view.ThreadedRenderer;

public class BackdropFrameRenderer extends Thread implements FrameCallback {
    private Drawable mCaptionBackgroundDrawable;
    private Choreographer mChoreographer;
    private DecorView mDecorView;
    private RenderNode mFrameAndBackdropNode;
    private boolean mFullscreen;
    private int mLastCaptionHeight;
    private int mLastContentHeight;
    private int mLastContentWidth;
    private int mLastXOffset;
    private int mLastYOffset;
    private ColorDrawable mNavigationBarColor;
    private final Rect mNewTargetRect;
    private boolean mOldFullscreen;
    private final Rect mOldStableInsets;
    private final Rect mOldSystemInsets;
    private final Rect mOldTargetRect;
    private ThreadedRenderer mRenderer;
    private boolean mReportNextDraw;
    private final int mResizeMode;
    private Drawable mResizingBackgroundDrawable;
    private final Rect mStableInsets;
    private ColorDrawable mStatusBarColor;
    private RenderNode mSystemBarBackgroundNode;
    private final Rect mSystemInsets;
    private final Rect mTargetRect;
    private Drawable mUserCaptionBackgroundDrawable;

    public BackdropFrameRenderer(DecorView decorView, ThreadedRenderer renderer, Rect initialBounds, Drawable resizingBackgroundDrawable, Drawable captionBackgroundDrawable, Drawable userCaptionBackgroundDrawable, int statusBarColor, int navigationBarColor, boolean fullscreen, Rect systemInsets, Rect stableInsets, int resizeMode) {
        this.mTargetRect = new Rect();
        this.mOldTargetRect = new Rect();
        this.mNewTargetRect = new Rect();
        this.mOldSystemInsets = new Rect();
        this.mOldStableInsets = new Rect();
        this.mSystemInsets = new Rect();
        this.mStableInsets = new Rect();
        setName("ResizeFrame");
        this.mRenderer = renderer;
        onResourcesLoaded(decorView, resizingBackgroundDrawable, captionBackgroundDrawable, userCaptionBackgroundDrawable, statusBarColor, navigationBarColor);
        this.mFrameAndBackdropNode = RenderNode.create("FrameAndBackdropNode", null);
        this.mRenderer.addRenderNode(this.mFrameAndBackdropNode, true);
        this.mTargetRect.set(initialBounds);
        this.mFullscreen = fullscreen;
        this.mOldFullscreen = fullscreen;
        this.mSystemInsets.set(systemInsets);
        this.mStableInsets.set(stableInsets);
        this.mOldSystemInsets.set(systemInsets);
        this.mOldStableInsets.set(stableInsets);
        this.mResizeMode = resizeMode;
        start();
    }

    void onResourcesLoaded(DecorView decorView, Drawable resizingBackgroundDrawable, Drawable captionBackgroundDrawableDrawable, Drawable userCaptionBackgroundDrawable, int statusBarColor, int navigationBarColor) {
        Drawable newDrawable;
        this.mDecorView = decorView;
        if (resizingBackgroundDrawable != null) {
            newDrawable = resizingBackgroundDrawable.getConstantState().newDrawable();
        } else {
            newDrawable = null;
        }
        this.mResizingBackgroundDrawable = newDrawable;
        if (captionBackgroundDrawableDrawable != null) {
            newDrawable = captionBackgroundDrawableDrawable.getConstantState().newDrawable();
        } else {
            newDrawable = null;
        }
        this.mCaptionBackgroundDrawable = newDrawable;
        if (userCaptionBackgroundDrawable != null) {
            newDrawable = userCaptionBackgroundDrawable.getConstantState().newDrawable();
        } else {
            newDrawable = null;
        }
        this.mUserCaptionBackgroundDrawable = newDrawable;
        if (this.mCaptionBackgroundDrawable == null) {
            this.mCaptionBackgroundDrawable = this.mResizingBackgroundDrawable;
        }
        if (statusBarColor != 0) {
            this.mStatusBarColor = new ColorDrawable(statusBarColor);
            addSystemBarNodeIfNeeded();
        } else {
            this.mStatusBarColor = null;
        }
        if (navigationBarColor != 0) {
            this.mNavigationBarColor = new ColorDrawable(navigationBarColor);
            addSystemBarNodeIfNeeded();
            return;
        }
        this.mNavigationBarColor = null;
    }

    private void addSystemBarNodeIfNeeded() {
        if (this.mSystemBarBackgroundNode == null) {
            this.mSystemBarBackgroundNode = RenderNode.create("SystemBarBackgroundNode", null);
            this.mRenderer.addRenderNode(this.mSystemBarBackgroundNode, false);
        }
    }

    public void setTargetRect(Rect newTargetBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets) {
        synchronized (this) {
            this.mFullscreen = fullscreen;
            this.mTargetRect.set(newTargetBounds);
            this.mSystemInsets.set(systemInsets);
            this.mStableInsets.set(stableInsets);
            pingRenderLocked(false);
        }
    }

    public void onConfigurationChange() {
        synchronized (this) {
            if (this.mRenderer != null) {
                this.mOldTargetRect.set(0, 0, 0, 0);
                pingRenderLocked(false);
            }
        }
    }

    public void releaseRenderer() {
        synchronized (this) {
            if (this.mRenderer != null) {
                this.mRenderer.setContentDrawBounds(0, 0, 0, 0);
                this.mRenderer.removeRenderNode(this.mFrameAndBackdropNode);
                if (this.mSystemBarBackgroundNode != null) {
                    this.mRenderer.removeRenderNode(this.mSystemBarBackgroundNode);
                }
                this.mRenderer = null;
                pingRenderLocked(false);
            }
        }
    }

    public void run() {
        try {
            Looper.prepare();
            synchronized (this) {
                this.mChoreographer = Choreographer.getInstance();
                Looper.loop();
                synchronized (this) {
                    this.mChoreographer = null;
                    Choreographer.releaseInstance();
                }
            }
        } finally {
            releaseRenderer();
        }
    }

    public void doFrame(long frameTimeNanos) {
        synchronized (this) {
            if (this.mRenderer == null) {
                reportDrawIfNeeded();
                Looper.myLooper().quit();
                return;
            }
            doFrameUncheckedLocked();
        }
    }

    private void doFrameUncheckedLocked() {
        this.mNewTargetRect.set(this.mTargetRect);
        if (this.mNewTargetRect.equals(this.mOldTargetRect) && this.mOldFullscreen == this.mFullscreen && this.mStableInsets.equals(this.mOldStableInsets) && this.mSystemInsets.equals(this.mOldSystemInsets)) {
            if (!this.mReportNextDraw) {
                return;
            }
        }
        this.mOldFullscreen = this.mFullscreen;
        this.mOldTargetRect.set(this.mNewTargetRect);
        this.mOldSystemInsets.set(this.mSystemInsets);
        this.mOldStableInsets.set(this.mStableInsets);
        redrawLocked(this.mNewTargetRect, this.mFullscreen, this.mSystemInsets, this.mStableInsets);
    }

    public boolean onContentDrawn(int xOffset, int yOffset, int xSize, int ySize) {
        boolean z = true;
        synchronized (this) {
            boolean firstCall = this.mLastContentWidth == 0;
            this.mLastContentWidth = xSize;
            this.mLastContentHeight = ySize - this.mLastCaptionHeight;
            this.mLastXOffset = xOffset;
            this.mLastYOffset = yOffset;
            this.mRenderer.setContentDrawBounds(this.mLastXOffset, this.mLastYOffset, this.mLastXOffset + this.mLastContentWidth, (this.mLastYOffset + this.mLastCaptionHeight) + this.mLastContentHeight);
            if (!firstCall) {
                z = false;
            } else if (this.mLastCaptionHeight == 0 && this.mDecorView.isShowingCaption()) {
                z = false;
            }
        }
        return z;
    }

    public void onRequestDraw(boolean reportNextDraw) {
        synchronized (this) {
            this.mReportNextDraw = reportNextDraw;
            this.mOldTargetRect.set(0, 0, 0, 0);
            pingRenderLocked(true);
        }
    }

    private void redrawLocked(Rect newBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets) {
        int captionHeight = this.mDecorView.getCaptionHeight();
        if (captionHeight != 0) {
            this.mLastCaptionHeight = captionHeight;
        }
        if ((this.mLastCaptionHeight != 0 || !this.mDecorView.isShowingCaption()) && this.mLastContentWidth != 0 && this.mLastContentHeight != 0) {
            int left = this.mLastXOffset + newBounds.left;
            int top = this.mLastYOffset + newBounds.top;
            int width = newBounds.width();
            int height = newBounds.height();
            this.mFrameAndBackdropNode.setLeftTopRightBottom(left, top, left + width, top + height);
            DisplayListCanvas canvas = this.mFrameAndBackdropNode.start(width, height);
            Drawable drawable = this.mUserCaptionBackgroundDrawable != null ? this.mUserCaptionBackgroundDrawable : this.mCaptionBackgroundDrawable;
            if (drawable != null) {
                drawable.setBounds(0, 0, left + width, this.mLastCaptionHeight + top);
                drawable.draw(canvas);
            }
            if (this.mResizingBackgroundDrawable != null) {
                this.mResizingBackgroundDrawable.setBounds(0, this.mLastCaptionHeight, left + width, top + height);
                this.mResizingBackgroundDrawable.draw(canvas);
            }
            this.mFrameAndBackdropNode.end(canvas);
            drawColorViews(left, top, width, height, fullscreen, systemInsets, stableInsets);
            this.mRenderer.drawRenderNode(this.mFrameAndBackdropNode);
            reportDrawIfNeeded();
        }
    }

    private void drawColorViews(int left, int top, int width, int height, boolean fullscreen, Rect systemInsets, Rect stableInsets) {
        if (this.mSystemBarBackgroundNode != null) {
            DisplayListCanvas canvas = this.mSystemBarBackgroundNode.start(width, height);
            this.mSystemBarBackgroundNode.setLeftTopRightBottom(left, top, left + width, top + height);
            int topInset = DecorView.getColorViewTopInset(this.mStableInsets.top, this.mSystemInsets.top);
            int bottomInset = DecorView.getColorViewBottomInset(stableInsets.bottom, systemInsets.bottom);
            int rightInset = DecorView.getColorViewRightInset(stableInsets.right, systemInsets.right);
            if (this.mStatusBarColor != null) {
                this.mStatusBarColor.setBounds(0, 0, left + width, topInset);
                this.mStatusBarColor.draw(canvas);
            }
            if (this.mNavigationBarColor != null && fullscreen) {
                int size = DecorView.getNavBarSize(bottomInset, rightInset);
                if (DecorView.isNavBarToRightEdge(bottomInset, rightInset)) {
                    this.mNavigationBarColor.setBounds(width - size, 0, width, height);
                } else {
                    this.mNavigationBarColor.setBounds(0, height - size, width, height);
                }
                this.mNavigationBarColor.draw(canvas);
            }
            this.mSystemBarBackgroundNode.end(canvas);
            this.mRenderer.drawRenderNode(this.mSystemBarBackgroundNode);
        }
    }

    private void reportDrawIfNeeded() {
        if (this.mReportNextDraw) {
            if (this.mDecorView.isAttachedToWindow()) {
                this.mDecorView.getViewRootImpl().reportDrawFinish();
            }
            this.mReportNextDraw = false;
        }
    }

    private void pingRenderLocked(boolean drawImmediate) {
        if (this.mChoreographer == null || drawImmediate) {
            doFrameUncheckedLocked();
        } else {
            this.mChoreographer.postFrameCallback(this);
        }
    }

    void setUserCaptionBackgroundDrawable(Drawable userCaptionBackgroundDrawable) {
        this.mUserCaptionBackgroundDrawable = userCaptionBackgroundDrawable;
    }
}
