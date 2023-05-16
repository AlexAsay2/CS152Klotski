package com.example.klotski;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BlockView extends View {
    private Block block;
    private int blockSize;
    private Paint paint;
    private Bitmap imageBitmap;

    public BlockView(Context context, Block block, int blockSize, int imgRsrc) {
        super(context);
        this.block = block;
        this.blockSize = blockSize;
        this.paint = new Paint();
        this.imageBitmap = BitmapFactory.decodeResource(getResources(), imgRsrc);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(blockSize, blockSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(imageBitmap, null, canvas.getClipBounds(), paint);
    }

    public Block getBlock() {
        return block;
    }
}

