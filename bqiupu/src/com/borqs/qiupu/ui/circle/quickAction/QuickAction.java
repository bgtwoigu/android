package com.borqs.qiupu.ui.circle.quickAction;

import android.content.Context;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;

import java.util.List;
import java.util.ArrayList;

import com.borqs.qiupu.R;
import com.borqs.qiupu.ui.bpc.QiupuComposeActivity;

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently supports vertical 
 * and horizontal layout.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 */
public class QuickAction extends PopupWindows implements OnDismissListener {
	
	private static final String TAG = "QuickAction";
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private ScrollView mScroller;
	private OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;

	private List<ActionItem> actionItems = new ArrayList<ActionItem>();

	private boolean mDidAction;

	private int mChildPos;
    private int mInsertPos;
    private int mAnimStyle;
    private int mOrientation;
    private int rootWidth=0;
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;
	
	private boolean mOnTop;
	
	private Animation mRoateTopAnimation;
	private Animation mRoateBottomAnimation;

    /**
     * Constructor for default vertical layout
     * 
     * @param context  Context
     */
    public QuickAction(Context context) {
        this(context, VERTICAL);
    }

    /**
     * Constructor allowing orientation override
     * 
     * @param context    Context
     * @param orientation Layout orientation, can be vartical or horizontal
     */
    public QuickAction(Context context, int orientation) {
        super(context);
        
        mOrientation = orientation;
        
        mInflater 	 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mOrientation == HORIZONTAL) {
            setRootViewId(R.layout.popup_horizontal);
        } else {
            setRootViewId(R.layout.popup_vertical);
        }

        mAnimStyle 	= ANIM_AUTO;
        mChildPos 	= 0;
        
        mRoateTopAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_tip_arrowup);
    	mRoateTopAnimation.setFillAfter(true);
    	
    	mRoateBottomAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_tip); 
    	mRoateBottomAnimation.setFillAfter(true);
    	
    }

    /**
     * Get action item at an index
     * 
     * @param index  Index of item (position from callback)
     * 
     * @return  Action Item at the position
     */
    public ActionItem getActionItem(int index) {
        return actionItems.get(index);
    }
    
	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootView	= (ViewGroup) mInflater.inflate(id, null);
		mTrack 		= (ViewGroup) mRootView.findViewById(R.id.tracks);

		mArrowDown 	= (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp 	= (ImageView) mRootView.findViewById(R.id.arrow_up);

		mScroller	= (ScrollView) mRootView.findViewById(R.id.scroller);

		//This was previously defined on show() method, moved here to prevent force close that occured
		//when tapping fastly on a view to show quickaction dialog.
		//Thanx to zammbi (github.com/zammbi)
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}

	/**
	 * Set animation style
	 * 
	 * @param mAnimStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}

	/**
	 * Set listener for action item clicked.
	 * 
	 * @param listener Listener
	 */
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem(ActionItem action) {
		actionItems.add(action);

		String title 	= action.getTitle();
		Drawable icon 	= action.getIcon();

		View container;

		if (mOrientation == HORIZONTAL) {
            container = mInflater.inflate(R.layout.action_item_horizontal, null);
        } else {
            container = mInflater.inflate(R.layout.action_item_vertical, null);
        }

		ImageView img 	= (ImageView) container.findViewById(R.id.iv_icon);
		TextView text 	= (TextView) container.findViewById(R.id.tv_title);
		
		if(mSelectedid == action.getActionId()) {
			text.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
		}

		if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}

		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}

		final int pos 		=  mChildPos;
		final long actionId 	= action.getActionId();

		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(QuickAction.this, pos, actionId);
                }

                if (!getActionItem(pos).isSticky()) {  
                	mDidAction = true;
                	
                    dismiss();
                }
			}
		});

		container.setFocusable(true);
		container.setClickable(true);

		if (mOrientation == HORIZONTAL && mChildPos != 0) {
            View separator = mInflater.inflate(R.layout.horiz_separator, null);
            
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
            
            separator.setLayoutParams(params);
            separator.setPadding(5, 0, 5, 0);
            
            mTrack.addView(separator, mInsertPos);
            
            mInsertPos++;
        }

		mTrack.addView(container, mInsertPos);

		mChildPos++;
		mInsertPos++;
	}

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */
	public void show (View anchor, View anchorIcon) {
		preShow();

		int xPos, yPos, arrowPos;

		mDidAction 			= false;

		int[] location 		= new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
		                	+ anchor.getHeight());

		//mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight 		= mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth		= mRootView.getMeasuredWidth();
		}

		int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight	= mWindowManager.getDefaultDisplay().getHeight();

		//automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos 		= anchorRect.left - (rootWidth-anchor.getWidth());			
			xPos 		= (xPos < 0) ? 0 : xPos;

			arrowPos 	= anchorRect.centerX()-xPos;

		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth/2);
			} else {
				xPos = anchorRect.left;
			}

			arrowPos = anchorRect.centerX()-xPos;
		}

		int dyTop			= anchorRect.top;
		int dyBottom		= screenHeight - anchorRect.bottom;

		mOnTop		= (dyTop > dyBottom) ? true : false;

		Log.d(TAG, "show: " + mOnTop);
		
		if(anchorIcon != null) {
			if(mOnTop) {
    	    	anchorIcon.startAnimation(mRoateTopAnimation);
			}else {
    	    	anchorIcon.startAnimation(mRoateBottomAnimation);
			}
		}
		
		if (mOnTop) {
			
			anchor.setBackgroundResource(R.drawable.info_category_title_bottom_line_bg);
			anchor.setPadding((int)mContext.getResources().getDimension(R.dimen.category_padding_left), 
	    			(int)mContext.getResources().getDimension(R.dimen.small_text_padding_top), (int)mContext.getResources().getDimension(R.dimen.category_padding_right), (int)mContext.getResources().getDimension(R.dimen.small_text_padding_bottom));
			if (rootHeight > dyTop) {
				yPos 			= 15;
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			anchor.setBackgroundResource(R.drawable.info_category_title_top_line_bg);
			anchor.setPadding((int)mContext.getResources().getDimension(R.dimen.category_padding_left), 
	    			(int)mContext.getResources().getDimension(R.dimen.small_text_padding_top), (int)mContext.getResources().getDimension(R.dimen.category_padding_right), (int)mContext.getResources().getDimension(R.dimen.small_text_padding_bottom));
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) { 
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyBottom;
			}
		}

//		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

		setAnimationStyle(screenWidth, anchorRect.centerX(), mOnTop);

		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth screen width
	 * @param requestedX distance from left edge
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
	 * 		  and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
		break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
       
        param.leftMargin = requestedX - arrowWidth / 2;
        
        hideArrow.setVisibility(View.INVISIBLE);
    }

	/**
	 * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
	 * by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener(QuickAction.OnDismissListener listener) {
		setOnDismissListener(this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss(mOnTop);
		}
	}

	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction source, int pos, long actionId);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss(boolean onTop);
	}
	

	private long mSelectedid = -11111;
	public void setSelectActionId(long selectedid) {
		mSelectedid = selectedid;
	}
}