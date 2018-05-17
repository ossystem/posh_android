package ru.jufy.myposh.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.main.MainActivity;
import ru.jufy.myposh.ui.utils.HttpPostAsyncTask;
import ru.jufy.myposh.ui.views.ClippingRelativeLayout;

/**
 * Created by BorisDev on 16.08.2017.
 */

public class TextEditorFragment extends Fragment {
    private static final int initialFillColor = 0xFF0099FF;
    private static final int initialFontColor = 0xFF000011;
    private View rootView;
    private ClippingRelativeLayout poshikEditor;
    FloatingActionButton fabCancel;
    EditText textEditor;
    ImageView circle;
    ImageView keyBoard;
    ImageView font;
    ImageView fontColor;
    ImageView fillColor;
    ImageView upload;
    private RecyclerView fontsView = null;
    private ColorPickerView fontColorPicker;
    private ColorPickerView fillColorPicker;
    private int currentFillColor = initialFillColor;
    private int currentFontColor = initialFontColor;

    private float initialTextSize;
    private float mScaleFactor = 1.0f;

    private float initialPosX;
    private float initialPosY;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;

    private float mRotationDegrees = 0.f;

    private ScaleGestureDetector mScaleDetector;
    private MoveGestureDetector mMoveDetector;
    private RotateGestureDetector mRotateDetector;

    public TextEditorFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_text_editor, container, false);

        fabCancel = (FloatingActionButton)rootView.findViewById(R.id.fab_cancel);
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showCurrentFragment();
            }
        });

        textEditor = (EditText)rootView.findViewById(R.id.textEditor);
        textEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateKeyboard();
            }
        });
        textEditor.setTextColor(initialFontColor);
        textEditor.setRotation(mRotationDegrees);

        circle = (ImageView)rootView.findViewById(R.id.circle);
        GradientDrawable bg = (GradientDrawable) circle.getDrawable();
        bg.setColor(initialFillColor);

        keyBoard = (ImageView) rootView.findViewById(R.id.iconKeyboard);
        keyBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateKeyboard();
            }
        });

        font = (ImageView) rootView.findViewById(R.id.iconFont);
        font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateFont();
            }
        });

        fontColor = (ImageView) rootView.findViewById(R.id.iconFontColor);
        fontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateFontColor();
            }
        });

        fillColor = (ImageView) rootView.findViewById(R.id.iconFill);
        fillColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateFillColor();
            }
        });

        upload = (ImageView) rootView.findViewById(R.id.iconUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        initialTextSize = textEditor.getTextSize();
        initialPosX = textEditor.getX();
        initialPosY = textEditor.getY();

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mMoveDetector = new MoveGestureDetector(getContext(), new MoveListener());
        mRotateDetector = new RotateGestureDetector(getContext(), new RotateListener());

        poshikEditor = (ClippingRelativeLayout) rootView.findViewById(R.id.poshikEditor);

        poshikEditor.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                mMoveDetector.onTouchEvent(event);
                mRotateDetector.onTouchEvent(event);

                float newSize = initialTextSize * mScaleFactor;
                textEditor.setTextSize(newSize);

                textEditor.setX(initialPosX + mFocusX);
                textEditor.setY(initialPosY + mFocusY);

                textEditor.setRotation(mRotationDegrees);

                return true;
            }
        });

        ((MainActivity)getActivity()).hideBottomNav();

        return rootView;
    }

    private void activateKeyboard() {
        inactivateAll();
        setIcon(keyBoard, R.drawable.icon_keyboard_red);
        textEditor.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textEditor, InputMethodManager.SHOW_IMPLICIT);
    }

    private void inactivateAll() {
        inactivateKeyboard();
        inactivateFont();
        inactivateFontColor();
        inactivateFillColor();
    }

    private void inactivateKeyboard() {
        setIcon(keyBoard, R.drawable.icon_keyboard_pink);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textEditor.getWindowToken(), 0);
    }

    private void activateFont() {
        inactivateAll();
        setIcon(font, R.drawable.icon_font_red);
        addFontsList();
    }

    private void addFontsList() {
        fontsView = new RecyclerView(getContext());
        fontsView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        fontsView.setLayoutManager(new LinearLayoutManager(getContext()));
        FontsAdapter adapter = new FontsAdapter(getContext());
        fontsView.setAdapter(adapter);
        ((LinearLayout)rootView).addView(fontsView);
    }

    private void inactivateFont() {
        setIcon(font, R.drawable.icon_font_pink);
        if (null != fontsView) {
            ((LinearLayout) rootView).removeView(fontsView);
            fontsView = null;
        }
    }

    private void activateFontColor() {
        inactivateAll();
        setIcon(fontColor, R.drawable.icon_font_color_red);
        fontColorPicker = new ColorPickerView(getContext());
        fontColorPicker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        fontColorPicker.setAlphaSliderVisible(true);
        fontColorPicker.setColor(currentFontColor, true);
        fontColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                currentFontColor = newColor;
                textEditor.setTextColor(newColor);
            }
        });

        // this wierd way to display color picker is because direct addition to rootView
        // causes crash at some devices (e.g. Nexus 7 virtual device)
        // The crash scenario is the following:
        // - soft keyboard is initially present at the screen and consumes the rest of the layout
        // - color picker is added to the end of the layout
        // - color picker is asked to draw itself
        // - color picker calculates the area left to occupy
        // - the whole layout haven't got recalculated its dimensions after keyboard hiding
        // - color picker founds out that it has not enough space and throws exception
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout)rootView).addView(fontColorPicker);
            }
        }, 500);
    }

    private void inactivateFontColor() {
        setIcon(fontColor, R.drawable.icon_font_color_pink);
        if (null != fontColorPicker) {
            ((LinearLayout) rootView).removeView(fontColorPicker);
            fontColorPicker = null;
        }
    }

    private void activateFillColor() {
        inactivateAll();
        setIcon(fillColor, R.drawable.icon_fill_red);
        fillColorPicker = new ColorPickerView(getContext());
        fillColorPicker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        fillColorPicker.setAlphaSliderVisible(true);
        fillColorPicker.setColor(currentFillColor, true);
        fillColorPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                currentFillColor = newColor;
                GradientDrawable bg = (GradientDrawable) circle.getDrawable();
                bg.setColor(newColor);
            }
        });
        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout)rootView).addView(fillColorPicker);
            }
        }, 500);
    }

    private void inactivateFillColor() {
        setIcon(fillColor, R.drawable.icon_fill_pink);
        if (null != fillColorPicker) {
            ((LinearLayout) rootView).removeView(fillColorPicker);
            fillColorPicker = null;
        }
    }

    private void uploadImage() {
        inactivateAll();
        ClippingRelativeLayout poshikEditor = (ClippingRelativeLayout) rootView.findViewById(R.id.poshikEditor);
        try {
            textEditor.setCursorVisible(false);
            poshikEditor.buildDrawingCache();
            Bitmap bm = poshikEditor.getDrawingCache();
            Bitmap output = Bitmap.createBitmap(bm.getHeight(),
                    bm.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawARGB(0, 0, 0, 0);
            Rect srcRect = new Rect((bm.getWidth() - bm.getHeight())/2, 0, (bm.getWidth() - bm.getHeight())/2 + bm.getHeight(), bm.getHeight());
            Rect newRect = new Rect(0, 0, bm.getHeight(), bm.getHeight());
            canvas.drawBitmap(bm, srcRect, newRect, null);

            Path clipPath = new Path();
            clipPath.addCircle(newRect.exactCenterX(), newRect.exactCenterY(), newRect.height()/2, Path.Direction.CW);
            canvas.clipPath(clipPath, Region.Op.DIFFERENCE);
            canvas.drawColor(currentFillColor);
            sendToServer(output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            poshikEditor.destroyDrawingCache();
            textEditor.setCursorVisible(true);
        }
    }

    private boolean sendToServer(Bitmap image) {
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setImage(image);
        String addPoshikRequest[] = new String[2];
        addPoshikRequest[0] = MyPoshApplication.Companion.getDOMAIN() + "poshiks/my";
        addPoshikRequest[1] = "Content-Disposition: form-data; name=\"poshik\"; filename=\"poshik.jpg\"" + postRequest.getCrLf()
                                + "Content-Type: image/jpeg" + postRequest.getCrLf() + postRequest.getCrLf();

        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Authorization", "Bearer " + MyPoshApplication.Companion.getCurrentToken().getToken());
        reqProps.put("Cache-Control", "no-cache");
        reqProps.put("Content-Type", "multipart/form-data; boundary=" + postRequest.getBoundary());
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(addPoshikRequest).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setIcon(ImageView view, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(getResources().getDrawable(id, MyPoshApplication.Companion.getContext().getTheme()));
        } else {
            view.setImageDrawable(getResources().getDrawable(id));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        activateKeyboard();
    }

    class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.FontHolder> {

        List<Pair<String, Typeface>> items;
        Context context;

        FontsAdapter(Context context) {
            super();
            this.context = context;
            this.items = getSSystemFontMap();
        }

        private List<Pair<String, Typeface>> getSSystemFontMap() {
            Map<String, Typeface> sSystemFontMap;
            List<Pair<String, Typeface>> result = new ArrayList<>();
            try {
                Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                Field f = Typeface.class.getDeclaredField("sSystemFontMap");
                f.setAccessible(true);
                sSystemFontMap = (Map<String, Typeface>) f.get(typeface);
                for (Map.Entry<String, Typeface> entry : sSystemFontMap.entrySet()) {
                    Pair<String, Typeface> font = new Pair<>(entry.getKey(), entry.getValue());
                    result.add(font);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public FontsAdapter.FontHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new FontsAdapter.FontHolder(v);
        }

        @Override
        public void onBindViewHolder(final FontsAdapter.FontHolder holder, int position) {
            holder.item.setText(items.get(position).first);
            holder.item.setTypeface(items.get(position).second);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textEditor.setTypeface(items.get(holder.getAdapterPosition()).second);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class FontHolder extends RecyclerView.ViewHolder {
            TextView item;
            FontHolder(View v) {
                super(v);
                item = (TextView)itemView.findViewById(R.id.list_item_text);
            }
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    private class MoveListener implements MoveGestureDetector.OnMoveGestureListener {
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            mFocusX += d.x;
            mFocusY += d.y;
            return true;
        }

        @Override
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {
        }
    }

    private class RotateListener implements RotateGestureDetector.OnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegrees -= detector.getRotationDegreesDelta();
            return true;
        }

        @Override
        public boolean onRotateBegin(RotateGestureDetector detector) {
            return true;
        }

        @Override
        public void onRotateEnd(RotateGestureDetector detector) {

        }
    }
}
