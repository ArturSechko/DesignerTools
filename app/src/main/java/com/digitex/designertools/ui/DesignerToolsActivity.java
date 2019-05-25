package com.digitex.designertools.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.digitex.designertools.R;
import com.digitex.designertools.utils.LaunchUtils;

public class DesignerToolsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_tools);
        if (!LaunchUtils.isCyanogenMod(this)) {
            TextView tv = (TextView) findViewById(R.id.qs_tiles_section);
            tv.setText(R.string.overlays_section_text);
        }
        View headerGlyph = findViewById(R.id.header_glyph);
        if (headerGlyph != null) {
            headerGlyph.setOnClickListener(mGlyphClickListener);
        }
    }

    private View.OnClickListener mGlyphClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(DesignerToolsActivity.this, CreditsActivity.class));
        }
    };
}
