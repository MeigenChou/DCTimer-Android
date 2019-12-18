package com.dctimer.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.util.Log;

import com.dctimer.activity.MainActivity;
import com.dctimer.aes.Decrypt;
import com.dctimer.model.SmartCube;

import java.util.*;

import static com.dctimer.APP.enterTime;

public class BluetoothTools {
    public static final String UUID_SUFFIX = "-0000-1000-8000-00805f9b34fb";
    public static final UUID SERVICE_UUID = UUID.fromString("0000180a" + UUID_SUFFIX);
    public static final UUID SERVICE_UUID_GAN = UUID.fromString("0000fff0" + UUID_SUFFIX);
    public static final UUID SERVICE_UUID_GIIKER = UUID.fromString("0000aadb" + UUID_SUFFIX);
    public static final UUID SERVICE_UUID_RW = UUID.fromString("0000aaaa" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_DATA = UUID.fromString("0000aadc" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_VERSION = UUID.fromString("00002a28" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_HARDWARE = UUID.fromString("00002a23" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_READ = UUID.fromString("0000aaab" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_WRITE = UUID.fromString("0000aaac" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_F2 = UUID.fromString("0000fff2" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_F3 = UUID.fromString("0000fff3" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_F5 = UUID.fromString("0000fff5" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_F6 = UUID.fromString("0000fff6" + UUID_SUFFIX);
    public static final UUID CHARACTER_UUID_F7 = UUID.fromString("0000fff7" + UUID_SUFFIX);

    private MainActivity context;
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Set<String> addressMap;
    private List<SmartCube> cubeList;
    private SmartCube cube;
    private SmartCube.StateChangedCallback callback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattService service;
    private List<Integer> preMoves = new ArrayList<>();
    private int prevMoveCnt = -1;
    private long lastTime;

    public BluetoothTools(MainActivity context) {
        this.context = context;
        cubeList = new ArrayList<>();
    }

    public boolean initBluetoothAdapter() {
        if (bluetoothAdapter == null) getBluetoothAdapter();
        return bluetoothAdapter != null;
    }

    public void getBluetoothAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            boolean isEnable = bluetoothAdapter.enable();
            if (!isEnable) {
                Log.e("dct", "蓝牙打开失败");
            }
        }
        mScanning = false;
    }

    public SmartCube getCube() {
        return cube;
    }

    public void setCubeStateChangeCallback(SmartCube.StateChangedCallback callback) {
        this.callback = callback;
    }

    @TargetApi(18)
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            Log.w("dct", "发现设备 "+bluetoothDevice.getName());
            if (bluetoothDevice.getName() == null) return;
            if (addressMap.contains(bluetoothDevice.getAddress())) return;
            SmartCube cube = new SmartCube(bluetoothDevice.getName(), bluetoothDevice.getAddress());
            addressMap.add(bluetoothDevice.getAddress());
            cubeList.add(cube);
            context.refreshCubeList(cubeList);
        }
    };

    @TargetApi(18)
    public void startScan() {
        cubeList = new ArrayList<>();
        if (bluetoothAdapter != null && !mScanning) {
            Log.w("dct", "搜索设备");
            addressMap = new HashSet<>();
            bluetoothAdapter.startLeScan(mLeScanCallback);
            mScanning = true;
        }
    }

    @TargetApi(18)
    public void stopScan() {
        if (bluetoothAdapter != null && mScanning) {
            Log.w("dct", "停止搜索");
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
    }

    public void addCube(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice.getName() == null) return;
        if (addressMap.contains(bluetoothDevice.getAddress())) return;
        SmartCube cube = new SmartCube(bluetoothDevice.getName(), bluetoothDevice.getAddress());
        addressMap.add(bluetoothDevice.getAddress());
        cubeList.add(cube);
    }

    @TargetApi(18)
    public void connectCube(int pos) {
        if (mScanning) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            context.showScanButton();
        }
        cube = cubeList.get(pos);
        String address = cube.getAddress();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        int connect = cube.getConnected();
        if (connect == 0) {
            cube.setConnected(2);
            context.refreshCubeList();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothGatt = device.connectGatt(context, false, mBluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
            } else mBluetoothGatt = device.connectGatt(context, false, mBluetoothGattCallback);
        }
    }

    @TargetApi(18)
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt = null;
        }
        cube = null;
    }

    @TargetApi(18)
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.w("dct", "设备已连接");
                context.dismissDialog();
                if (cube != null) {
                    cube.setConnected(1);
                    cube.setType(enterTime == 2 ? SmartCube.GAN_CUBE : SmartCube.GIIKER_CUBE);
                    cube.setStateChangedCallback(callback);
                }
                gatt.discoverServices();
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.w("dct", "连接断开");
                gatt.close();
                //mBluetoothGatt = null;
                if (cube != null) {
                    cube.setConnected(0);
                    context.disconnectHint(cube);
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (enterTime == 2)
                service = gatt.getService(SERVICE_UUID);
            else service = gatt.getService(SERVICE_UUID_RW);
            if (service == null)
                Log.e("dct", "service为null");
            else if (enterTime == 2) {
                BluetoothGattCharacteristic chr = service.getCharacteristic(CHARACTER_UUID_VERSION);
                if (chr == null) {
                    Log.e("dct", "获取设备版本失败");
                } else {
                    gatt.readCharacteristic(chr);
                }
            } else {
                //BluetoothGattCharacteristic chread = service.getCharacteristic(CHARACTER_UUID_READ);
//                if (gatt.setCharacteristicNotification(chread, true)) {
//                    Log.w("dct", "电量监听");
//                    for (BluetoothGattDescriptor descriptor : chread.getDescriptors()){
//                        if ((chread.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
//                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                        } else if ((chread.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
//                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
//                        }
//                        gatt.writeDescriptor(descriptor);
//                    }
//                } else Log.e("dct", "无法监听");
                //gatt.readCharacteristic(chread);
//                try {
//                    Thread.sleep(200);
//                } catch (Exception e) {}
//                BluetoothGattCharacteristic chwrite = service.getCharacteristic(CHARACTER_UUID_WRITE);
//                if (chwrite == null) {
//                    Log.e("dct", "获取电量信息失败");
//                } else {
//                    Log.w("dct", "获取电量");
//                    chwrite.setValue(new byte[] {-75});
//                    gatt.writeCharacteristic(chwrite);
//                }
//                try {
//                    Thread.sleep(200);
//                } catch (Exception e) {}
                service = gatt.getService(SERVICE_UUID_GIIKER);
                if (service == null) Log.e("dct", "service为null");
                else {
                    Log.w("dct", "获取数据");
                    BluetoothGattCharacteristic chr = service.getCharacteristic(CHARACTER_UUID_DATA);
                    gatt.readCharacteristic(chr);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            byte[] value = characteristic.getValue();
            //Log.w("dct", "uuid "+uuid.toString()+" value "+Arrays.toString(value));
            if (uuid.equals(CHARACTER_UUID_DATA)) {
                Log.w("dct", "value "+ Arrays.toString(value));
                byte[] valhex = Decrypt.toHexValue(value);
                Log.w("dct", "valhex "+Arrays.toString(valhex));
                String cubeState = Utils.parseGiikerState(valhex);
                Log.w("dct", "state " + cubeState);
                cube.setCubeState(cubeState);
                if (gatt.setCharacteristicNotification(characteristic, true)) {
                    for (BluetoothGattDescriptor descriptor: characteristic.getDescriptors()) {
                        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        }
                        gatt.writeDescriptor(descriptor);
                    }
                } else Log.e("dct", "无法监听");
            } else if (uuid.equals(CHARACTER_UUID_READ)) {
                Log.w("dct", "read "+Arrays.toString(value));
                service = gatt.getService(SERVICE_UUID_GIIKER);
                if (service == null) Log.e("dct", "service为null");
                else {
                    BluetoothGattCharacteristic chr = service.getCharacteristic(CHARACTER_UUID_DATA);
                    gatt.readCharacteristic(chr);
                }
            }
            if (uuid.equals(CHARACTER_UUID_VERSION)) {
                int version = (value[0] & 0xff) << 16 | (value[1] & 0xff) << 8 | (value[2] & 0xff);
                Log.w("dct", "版本号 "+version);
                cube.setVersion(version);
                if (version > 0x10007 && (version & 0xfffe00) == 0x010000) {
                    //BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID);
                    if (service == null) Log.e("dct", "service为null");
                    else {
                        BluetoothGattCharacteristic chhw = service.getCharacteristic(CHARACTER_UUID_HARDWARE);
                        gatt.readCharacteristic(chhw);
                    }
                } else Log.e("dct", "不支持的版本");
            } else if (uuid.equals(CHARACTER_UUID_HARDWARE)) {
                byte[] key = Decrypt.getKey(cube.getVersion(), value);
                if (key == null) Log.e("dct", "不支持的硬件");
                else {
                    Log.w("dct", "key "+ StringUtils.binaryArray(key));
                    Decrypt.initAES(key);
                    service = gatt.getService(SERVICE_UUID_GAN);
                    if (service == null) Log.e("dct", "service为null");
                    else {
                        BluetoothGattCharacteristic chf2 = service.getCharacteristic(CHARACTER_UUID_F2);
                        gatt.readCharacteristic(chf2);
                    }
                }
            } else if (uuid.equals(CHARACTER_UUID_F2)) {
                //Log.w("dct", "cube state " + StringUtils.binaryArray(value));
                value = Decrypt.decode(value);
                Log.w("dct", "cube decode "+StringUtils.binaryArray(value));
                String state = Utils.getCubeState(value);
                Log.w("dct", "state "+state);
                int check = cube.setCubeState(state);
                Log.w("dct", "check "+check);
                if (check != 0) {
                    cube.setCubeState("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
                }
                Log.w("dct", "facelet " + cube.getCubeState());
                if (service == null) Log.e("dct", "service为null");
                else {
                    BluetoothGattCharacteristic chf7 = service.getCharacteristic(CHARACTER_UUID_F7);
                    gatt.readCharacteristic(chf7);
                }
            } else if (uuid.equals(CHARACTER_UUID_F3)) {
                //Log.w("dct", "f3 data "+StringUtils.binaryArray(value));
                value = Decrypt.decode(value);
                Log.w("dct", "f3 decode "+StringUtils.binaryArray(value));
                if (service == null) Log.e("dct", "service为null");
                else {
                    BluetoothGattCharacteristic chf5 = service.getCharacteristic(CHARACTER_UUID_F5);
                    gatt.readCharacteristic(chf5);
                }
            }
            else if (uuid.equals(CHARACTER_UUID_F5)) {
                //Log.w("dct", "gyro state "+StringUtils.binaryArray(value));
                value = Decrypt.decode(value);
                //Log.w("dct", "gyro decode "+StringUtils.binaryArray(value));
                int moveCnt = value[12] & 0xff;
                if (prevMoveCnt < 0) prevMoveCnt = moveCnt;
                if (moveCnt == prevMoveCnt) {
                    if (service == null) Log.e("dct", "service为null");
                    else {
                        long timePassed = System.currentTimeMillis() - lastTime;
                        if (timePassed >= 60000) {  //获取电量
                            lastTime = System.currentTimeMillis();
                            BluetoothGattCharacteristic chf7 = service.getCharacteristic(CHARACTER_UUID_F7);
                            gatt.readCharacteristic(chf7);
                        } else {
                            try {
                                Thread.sleep(200);
                            } catch (Exception e) { }
                            BluetoothGattCharacteristic chf5 = service.getCharacteristic(CHARACTER_UUID_F5);
                            gatt.readCharacteristic(chf5);
                        }
                    }
                } else {
                    int moves = moveCnt - prevMoveCnt;
                    if (moves < 0) moves += 256;
                    prevMoveCnt = moveCnt;
                    StringBuilder sb = new StringBuilder();
                    for (int i=0; i<6; i++) {
                        int m = value[13 + i];
                        sb.append("URFDLB".charAt(m/3)).append(" 2'".charAt(m%3)).append(" ");
                        if (i >= 6 - moves) {
                            preMoves.add(m);
                        }
                    }
                    //Log.w("dct", "move data "+sb.toString());
                    //BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID_GAN);
                    if (service == null) Log.e("dct", "service为null");
                    else {
                        BluetoothGattCharacteristic chf6 = service.getCharacteristic(CHARACTER_UUID_F6);
                        gatt.readCharacteristic(chf6);
                    }
                }
            } else if (uuid.equals(CHARACTER_UUID_F6)) {
                //Log.w("dct", "move data "+StringUtils.binaryArray(value));
                value = Decrypt.decode(value);
                //Log.w("dct", "move decode "+StringUtils.binaryArray(value));
                int[] timeOffset = new int[9];
                for (int i = 0; i < 9; i++) {
                    timeOffset[i] = (value[i * 2 + 1] & 0xff) | (value[i * 2 + 2] & 0xff) << 8;
                }
                //Log.w("dct", "time off "+Arrays.toString(timeOffset));
                if (preMoves.size() > 0) {
                    int start = 9 - preMoves.size();
                    for (int i = 0; i < preMoves.size(); i++) {
                        int move = preMoves.get(i);
                        int time = timeOffset[start++];
                        context.moveCube(cube, move, time);
                    }
                    preMoves.clear();
                }
                //BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID_GAN);
                if (service == null) Log.e("dct", "service为null");
                else {
                    BluetoothGattCharacteristic chf5 = service.getCharacteristic(CHARACTER_UUID_F5);
                    gatt.readCharacteristic(chf5);
                }
            } else if (uuid.equals(CHARACTER_UUID_F7)) {
                //Log.w("dct", "f7 data "+StringUtils.binaryArray(value));
                lastTime = System.currentTimeMillis();
                byte[] decode = Decrypt.decode(value);
                //Log.w("dct", "f7 decode "+StringUtils.binaryArray(decode));
                //String address = gatt.getDevice().getAddress();
                Log.w("dct", "电池电量 "+decode[7]);
                if (cube != null)
                    cube.setBatteryValue(decode[7]);
                else Log.e("dct", "cube为null");
                //BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID_GAN);
                if (service == null) Log.e("dct", "service为null");
                else {
                    BluetoothGattCharacteristic chf5 = service.getCharacteristic(CHARACTER_UUID_F5);
                    gatt.readCharacteristic(chf5);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            Log.w("dct", "write "+uuid.toString()+" status "+status + " value "+Arrays.toString(characteristic.getValue()));
            if (uuid.equals(CHARACTER_UUID_WRITE)) {
//                BluetoothGattCharacteristic chr = service.getCharacteristic(CHARACTER_UUID_READ);
//                if (chr == null) {
//                    Log.e("dct", "获取设备信息失败");
//                } else {
//                    Log.w("dct", "获取电量");
//                    gatt.readCharacteristic(chr);
//                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            byte[] value = characteristic.getValue();
            Log.w("dct", "value changed "+uuid.toString()+" value "+Arrays.toString(value));
            if (uuid.equals(CHARACTER_UUID_DATA)) {
                byte[] valhex = Decrypt.toHexValue(value);
                //Log.w("dct", "valhex "+Arrays.toString(valhex));
                String cubeState = Utils.parseGiikerState(valhex);
                Log.w("dct", "state " + cubeState);
                int check = cube.setCubeState(cubeState);
                if (check != 0) cube.setCubeState("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
//                StringBuilder sb = new StringBuilder();
//                for (int i = 32; i < Math.min(40, valhex.length); i += 2) {
//                    sb.append("BDLURF".charAt(valhex[i] - 1)).append(" 2'".charAt((valhex[i + 1] - 1) % 7)).append(' ');
//                }
//                Log.w("dct", "move data "+sb.toString());
                int[] moveIdx = {5, 3, 4, 0, 1, 2};
                int move = moveIdx[valhex[32] - 1] * 3 + (valhex[33] - 1) % 7;
                long timeNow = System.currentTimeMillis();
                int time;
                if (lastTime == -1) {
                    time = 65535;
                } else {
                    time = (int) (timeNow - lastTime);
                    if (time > 65535) time = 65535;
                }
                context.moveCube(cube, move, time);
                lastTime = timeNow;
            }
        }
    };
}
