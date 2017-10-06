package com.example.android.bluetoothlegatt;
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import com.example.android.bluetoothlegatt.InputManagerCompat;
        import com.example.android.bluetoothlegatt.InputManagerCompat.InputDeviceListener;

        import android.annotation.SuppressLint;
        import android.annotation.TargetApi;
        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Paint;
        import android.graphics.Paint.Style;
        import android.graphics.Path;
        import android.os.Build;
        import android.os.SystemClock;
        import android.os.Vibrator;
        import android.util.AttributeSet;
        import android.util.SparseArray;
        import android.view.InputDevice;
        import android.view.KeyEvent;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Random;

/*
 * A trivial joystick based physics game to demonstrate joystick handling. If
 * the game controller has a vibrator, then it is used to provide feedback when
 * a bullet is fired or the ship crashes into an obstacle. Otherwise, the system
 * vibrator is used for that purpose.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class GameView extends View implements InputDeviceListener {

    private static final int DPAD_STATE_LEFT = 1 << 0;
    private static final int DPAD_STATE_RIGHT = 1 << 1;
    private static final int DPAD_STATE_UP = 1 << 2;
    private static final int DPAD_STATE_DOWN = 1 << 3;

    private final Random mRandom;
    private long mLastStepTime;
    private final InputManagerCompat mInputManager;

    private final float mBaseSpeed;

//    private int currentJoystickController;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRandom = new Random();

        setFocusable(true);
        setFocusableInTouchMode(true);

        float baseSize = getContext().getResources().getDisplayMetrics().density * 5f;
        mBaseSpeed = baseSize * 3;

        mInputManager = InputManagerCompat.Factory.getInputManager(this.getContext());
        mInputManager.registerInputDeviceListener(this, null);
    }

//    // Iterate through the input devices, looking for controllers. Create a ship
//    // for every device that reports itself as a gamepad or joystick.
//    void findControllersAndAttachPepes() {
//        int[] deviceIds = mInputManager.getInputDeviceIds();
//        for (int deviceId : deviceIds) {
//            InputDevice dev = mInputManager.getInputDevice(deviceId);
//            int sources = dev.getSources();
//            // if the device is a gamepad/joystick, create a ship to represent it
//            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
//                    ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
//                // if the device has a gamepad or joystick
//                // TODO: add code to link controller to a pepe? Potentially add multiple Pepes here
////                getPepeForId(deviceId);
//            }
//        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int deviceId = event.getDeviceId();
        if (deviceId != -1) {
            // TODO: get Pepe for this controller - dont need to, only 1 pepe
//            Ship currentShip = getShipForId(deviceId);
            // TODO: do pepe thing for this button action - call to appropriate function from button.
//            if (keyCode == )
            Toast.makeText(super.getContext(), "keycode = " + keyCode, Toast.LENGTH_SHORT).show();

//            if (currentShip.onKeyDown(keyCode, event)) {
//                step(event.getEventTime());
//                return true;
//            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int deviceId = event.getDeviceId();
        if (deviceId != -1) {
            // TODO: get Pepe for this controller
//            Ship currentShip = getShipForId(deviceId);
            // TODO: do pepe thing for this button action
//            if (currentShip.onKeyUp(keyCode, event)) {
//                step(event.getEventTime());
//                return true;
//            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        mInputManager.onGenericMotionEvent(event);

        // Check that the event came from a joystick or gamepad since a generic
        // motion event could be almost anything. API level 18 adds the useful
        // event.isFromSource() helper function.
        int eventSource = event.getSource();
        if ((((eventSource & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                ((eventSource & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK))
                && event.getAction() == MotionEvent.ACTION_MOVE) {
            int id = event.getDeviceId();
            if (-1 != id) {
                // TODO: get pepe
//                Ship curShip = getShipForId(id);
                // TODO: do thing with pepe
//                if (curShip.onGenericMotionEvent(event)) {
//                    return true;
//                }
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        // Turn on and off animations based on the window focus.
        // Alternately, we could update the game state using the Activity
        // onResume()
        // and onPause() lifecycle events.
        if (hasWindowFocus) {
            mLastStepTime = SystemClock.uptimeMillis();
            mInputManager.onResume();
            // TODO: reset position?
//        } else {
//            int numShips = mShips.size();
//            for (int i = 0; i < numShips; i++) {
//                Ship currentShip = mShips.valueAt(i);
//                if (currentShip != null) {
//                    currentShip.setHeading(0, 0);
//                    currentShip.setVelocity(0, 0);
//                    currentShip.mDPadState = 0;
//                }
//            }
            mInputManager.onPause();
        }

        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Reset the game when the view changes size.
        reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Update the animation
        // TODO: no need to draw, do we have to do something else?
//        animateFrame();

//        // Draw the ships.
//        int numShips = mShips.size();
//        for (int i = 0; i < numShips; i++) {
//            Ship currentShip = mShips.valueAt(i);
//            if (currentShip != null) {
//                currentShip.draw(canvas);
//            }
//        }
//
//        // Draw bullets.
//        int numBullets = mBullets.size();
//        for (int i = 0; i < numBullets; i++) {
//            final Bullet bullet = mBullets.get(i);
//            bullet.draw(canvas);
//        }
//
//        // Draw obstacles.
//        int numObstacles = mObstacles.size();
//        for (int i = 0; i < numObstacles; i++) {
//            final Obstacle obstacle = mObstacles.get(i);
//            obstacle.draw(canvas);
//        }
    }

//    /**
//     * Uses the device descriptor to try to assign the same color to the same
//     * joystick. If there are two joysticks of the same type connected over USB,
//     * or the API is < API level 16, it will be unable to distinguish the two
//     * devices.
//     *
//     * @param shipID
//     * @return
//     */
    // TODO: getPepeForId?!?!?!?!
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private Ship getPepeForId(int shipID) {
//        Ship currentShip = mShips.get(shipID);
//        if (null == currentShip) {
//
//            // do we know something about this ship already?
//            InputDevice dev = InputDevice.getDevice(shipID);
//            String deviceString = null;
//            Integer shipColor = null;
//            if (null != dev) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    deviceString = dev.getDescriptor();
//                } else {
//                    deviceString = dev.getName();
//                }
//                shipColor = mDescriptorMap.get(deviceString);
//            }
//
//            if (null != shipColor) {
//                int color = shipColor;
//                int numShips = mShips.size();
//                // do we already have a ship with this color?
//                for (int i = 0; i < numShips; i++) {
//                    if (mShips.valueAt(i).getColor() == color) {
//                        shipColor = null;
//                        // we won't store this value either --- if the first
//                        // controller gets disconnected/connected, it will get
//                        // the same color.
//                        deviceString = null;
//                    }
//                }
//            }
//            if (null != shipColor) {
//                currentShip = new Ship(shipColor);
//                if (null != deviceString) {
//                    mDescriptorMap.remove(deviceString);
//                }
//            } else {
//                currentShip = new Ship(getNextShipColor());
//            }
//            mShips.append(shipID, currentShip);
//            currentShip.setInputDevice(dev);
//
//            if (null != deviceString) {
//                mDescriptorMap.put(deviceString, currentShip.getColor());
//            }
//        }
//        return currentShip;
//    }

//    /**
//     * Remove the ship from the array of active ships by ID.
//     *
//     * @param shipID
//     */
    // TODO Remove Pepe ID?
//    private void removeShipForID(int shipID) {
//        mShips.remove(shipID);
//    }

    private void reset() {
        // TODO: Reset something?
//        mShips.clear();
//        mBullets.clear();
//        mObstacles.clear();
//        findControllersAndAttachShips();
    }

    private void animateFrame() {
        long currentStepTime = SystemClock.uptimeMillis();
        step(currentStepTime);
        invalidate();
    }

    private void step(long currentStepTime) {
        float tau = (currentStepTime - mLastStepTime) * 0.001f;
        mLastStepTime = currentStepTime;
        //TODO: Do it live!!! What should be done here?!? Main control loop on a timer...
//        // Move the ships
//        int numShips = mShips.size();
//        for (int i = 0; i < numShips; i++) {
//            Ship currentShip = mShips.valueAt(i);
//            if (currentShip != null) {
//                currentShip.accelerate(tau);
//                if (!currentShip.step(tau)) {
//                    currentShip.reincarnate();
//                }
//            }
//        }
//
//        // Move the bullets.
//        int numBullets = mBullets.size();
//        for (int i = 0; i < numBullets; i++) {
//            final Bullet bullet = mBullets.get(i);
//            if (!bullet.step(tau)) {
//                mBullets.remove(i);
//                i -= 1;
//                numBullets -= 1;
//            }
//        }
//
//        // Move obstacles.
//        int numObstacles = mObstacles.size();
//        for (int i = 0; i < numObstacles; i++) {
//            final Obstacle obstacle = mObstacles.get(i);
//            if (!obstacle.step(tau)) {
//                mObstacles.remove(i);
//                i -= 1;
//                numObstacles -= 1;
//            }
//        }
//
//        // Check for collisions between bullets and obstacles.
//        for (int i = 0; i < numBullets; i++) {
//            final Bullet bullet = mBullets.get(i);
//            for (int j = 0; j < numObstacles; j++) {
//                final Obstacle obstacle = mObstacles.get(j);
//                if (bullet.collidesWith(obstacle)) {
//                    bullet.destroy();
//                    obstacle.destroy();
//                    break;
//                }
//            }
//        }
//
//        // Check for collisions between the ship and obstacles --- this could
//        // get slow
//        for (int i = 0; i < numObstacles; i++) {
//            final Obstacle obstacle = mObstacles.get(i);
//            for (int j = 0; j < numShips; j++) {
//                Ship currentShip = mShips.valueAt(j);
//                if (currentShip != null) {
//                    if (currentShip.collidesWith(obstacle)) {
//                        currentShip.destroy();
//                        obstacle.destroy();
//                        break;
//                    }
//                }
//            }
//        }
//
//        // Spawn more obstacles offscreen when needed.
//        // Avoid putting them right on top of the ship.
//        int tries = MAX_OBSTACLES - mObstacles.size() + 10;
//        final float minDistance = mShipSize * 4;
//        while (mObstacles.size() < MAX_OBSTACLES && tries-- > 0) {
//            float size = mRandom.nextFloat() * (mMaxObstacleSize - mMinObstacleSize)
//                    + mMinObstacleSize;
//            float positionX, positionY;
//            int edge = mRandom.nextInt(4);
//            switch (edge) {
//                case 0:
//                    positionX = -size;
//                    positionY = mRandom.nextInt(getHeight());
//                    break;
//                case 1:
//                    positionX = getWidth() + size;
//                    positionY = mRandom.nextInt(getHeight());
//                    break;
//                case 2:
//                    positionX = mRandom.nextInt(getWidth());
//                    positionY = -size;
//                    break;
//                default:
//                    positionX = mRandom.nextInt(getWidth());
//                    positionY = getHeight() + size;
//                    break;
//            }
//            boolean positionSafe = true;
//
//            // If the obstacle is too close to any ships, we don't want to
//            // spawn it.
//            for (int i = 0; i < numShips; i++) {
//                Ship currentShip = mShips.valueAt(i);
//                if (currentShip != null) {
//                    if (currentShip.distanceTo(positionX, positionY) < minDistance) {
//                        // try to spawn again
//                        positionSafe = false;
//                        break;
//                    }
//                }
//            }
//
//            // if the position is safe, add the obstacle and reset the retry
//            // counter
//            if (positionSafe) {
//                tries = MAX_OBSTACLES - mObstacles.size() + 10;
//                // we can add the obstacle now since it isn't close to any ships
//                float direction = mRandom.nextFloat() * (float) Math.PI * 2;
//                float speed = mRandom.nextFloat() * (mMaxObstacleSpeed - mMinObstacleSpeed)
//                        + mMinObstacleSpeed;
//                float velocityX = (float) Math.cos(direction) * speed;
//                float velocityY = (float) Math.sin(direction) * speed;
//
//                Obstacle obstacle = new Obstacle();
//                obstacle.setPosition(positionX, positionY);
//                obstacle.setSize(size);
//                obstacle.setVelocity(velocityX, velocityY);
//                mObstacles.add(obstacle);
//            }
//        }
    }

    private static float pythag(float x, float y) {
        return (float) Math.sqrt(x * x + y * y);
    }

    private static int blend(float alpha, int from, int to) {
        return from + (int) ((to - from) * alpha);
    }

    private static void setPaintARGBBlend(Paint paint, float alpha,
                                          int a1, int r1, int g1, int b1,
                                          int a2, int r2, int g2, int b2) {
        paint.setARGB(blend(alpha, a1, a2), blend(alpha, r1, r2),
                blend(alpha, g1, g2), blend(alpha, b1, b2));
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device,
                                         int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        if (range != null) {
            final float flat = range.getFlat();
            final float value = historyPos < 0 ? event.getAxisValue(axis)
                    : event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            // A joystick at rest does not always report an absolute position of
            // (0,0).
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    /**
     * Any gamepad button + the spacebar or DPAD_CENTER will be used as the fire
     * key.
     *
     * @param keyCode
     * @return true of it's a fire key.
     */
    private static boolean isFireKey(int keyCode) {
        return KeyEvent.isGamepadButton(keyCode)
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_SPACE;
    }

    private abstract class Sprite {
        protected float mPositionX;
        protected float mPositionY;
        protected float mVelocityX;
        protected float mVelocityY;
        protected float mSize;
        protected boolean mDestroyed;
        protected float mDestroyAnimProgress;

        public void setPosition(float x, float y) {
            mPositionX = x;
            mPositionY = y;
        }

        public void setVelocity(float x, float y) {
            mVelocityX = x;
            mVelocityY = y;
        }

        public void setSize(float size) {
            mSize = size;
        }

        public float distanceTo(float x, float y) {
            return pythag(mPositionX - x, mPositionY - y);
        }

        public float distanceTo(Sprite other) {
            return distanceTo(other.mPositionX, other.mPositionY);
        }

        public boolean collidesWith(Sprite other) {
            // Really bad collision detection.
            return !mDestroyed && !other.mDestroyed
                    && distanceTo(other) <= Math.max(mSize, other.mSize)
                    + Math.min(mSize, other.mSize) * 0.5f;
        }

        public boolean isDestroyed() {
            return mDestroyed;
        }

        /**
         * Moves the sprite based on the elapsed time defined by tau.
         *
         * @param tau the elapsed time in seconds since the last step
         * @return false if the sprite is to be removed from the display
         */
        public boolean step(float tau) {
            mPositionX += mVelocityX * tau;
            mPositionY += mVelocityY * tau;

            if (mDestroyed) {
                mDestroyAnimProgress += tau / getDestroyAnimDuration();
                if (mDestroyAnimProgress >= getDestroyAnimCycles()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Draws the sprite.
         *
         * @param canvas the Canvas upon which to draw the sprite.
         */
        public abstract void draw(Canvas canvas);

        /**
         * Returns the duration of the destruction animation of the sprite in
         * seconds.
         *
         * @return the float duration in seconds of the destruction animation
         */
        public abstract float getDestroyAnimDuration();

        /**
         * Returns the number of cycles to play the destruction animation. A
         * destruction animation has a duration and a number of cycles to play
         * it for, so we can have an extended death sequence when a ship or
         * object is destroyed.
         *
         * @return the float number of cycles to play the destruction animation
         */
        public abstract float getDestroyAnimCycles();

        protected boolean isOutsidePlayfield() {
            final int width = GameView.this.getWidth();
            final int height = GameView.this.getHeight();
            return mPositionX < 0 || mPositionX >= width
                    || mPositionY < 0 || mPositionY >= height;
        }

        protected void wrapAtPlayfieldBoundary() {
            final int width = GameView.this.getWidth();
            final int height = GameView.this.getHeight();
            while (mPositionX <= -mSize) {
                mPositionX += width + mSize * 2;
            }
            while (mPositionX >= width + mSize) {
                mPositionX -= width + mSize * 2;
            }
            while (mPositionY <= -mSize) {
                mPositionY += height + mSize * 2;
            }
            while (mPositionY >= height + mSize) {
                mPositionY -= height + mSize * 2;
            }
        }

        public void destroy() {
            mDestroyed = true;
            step(0);
        }
    }

    private static int sShipColor = 0;

    /**
     * Returns the next ship color in the sequence. Very simple. Does not in any
     * way guarantee that there are not multiple ships with the same color on
     * the screen.
     *
     * @return an int containing the index of the next ship color
     */
    private static int getNextShipColor() {
        int color = sShipColor & 0x07;
        if (0 == color) {
            color++;
            sShipColor++;
        }
        sShipColor++;
        return color;
    }

    /*
     * Static constants associated with Ship inner class
     */
    private static final long[] sDestructionVibratePattern = new long[] {
            0, 20, 20, 40, 40, 80, 40, 300
    };

    /*
     * When an input device is added, we add a ship based upon the device.
     * @see
     * com.example.inputmanagercompat.InputManagerCompat.InputDeviceListener
     * #onInputDeviceAdded(int)
     */
    @Override
    public void onInputDeviceAdded(int deviceId) {
        // TODO: getPepeForId()
//        getShipForId(deviceId);
    }

    /*
     * This is an unusual case. Input devices don't typically change, but they
     * certainly can --- for example a device may have different modes. We use
     * this to make sure that the ship has an up-to-date InputDevice.
     * @see
     * com.example.inputmanagercompat.InputManagerCompat.InputDeviceListener
     * #onInputDeviceChanged(int)
     */
    @Override
    public void onInputDeviceChanged(int deviceId) {
        // TODO: something about getting a pepe ID
//        Ship ship = getShipForId(deviceId);
//        ship.setInputDevice(InputDevice.getDevice(deviceId));
    }

    /*
     * Remove any ship associated with the ID.
     * @see
     * com.example.inputmanagercompat.InputManagerCompat.InputDeviceListener
     * #onInputDeviceRemoved(int)
     */
    @Override
    public void onInputDeviceRemoved(int deviceId) {
        // TODO: remove a pepe
//        removeShipForID(deviceId);
    }
}
