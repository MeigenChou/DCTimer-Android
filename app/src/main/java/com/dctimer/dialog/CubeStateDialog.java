package com.dctimer.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dctimer.R;
import com.dctimer.activity.MainActivity;
import com.dctimer.model.SmartCube;
import com.dctimer.model.Timer;
import com.dctimer.util.Utils;

public class CubeStateDialog extends DialogFragment {
    private SmartCube cube;
    private TextView tvBattery;
    private ImageView ivBattery;
    private ImageView imageView;

    public static CubeStateDialog newInstance(SmartCube cube) {
        CubeStateDialog dialog = new CubeStateDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cube", cube);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        cube = (SmartCube) getArguments().getSerializable("cube");
        AlertDialog.Builder buidler = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_cube_state, null);
        tvBattery = view.findViewById(R.id.tv_battery);
        int batteryValue = cube.getBatteryValue();
        tvBattery.setText(batteryValue + "%");
        ivBattery = view.findViewById(R.id.iv_battery);
        setBatteryImage(batteryValue);
        imageView = view.findViewById(R.id.image_view);
        setImage();
        Button btRefresh = view.findViewById(R.id.btn_refresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //BluetoothGatt mBluetoothGatt = connectedDevices.get(cube.getAddress());
                int batteryValue = cube.getBatteryValue();
                tvBattery.setText(batteryValue + "%");
                setBatteryImage(batteryValue);
                setImage();
            }
        });
        Button btMarkSolve = view.findViewById(R.id.bt_solved);
        btMarkSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cube.markSolved();
                setImage();
            }
        });
        Button btScrambled = view.findViewById(R.id.bt_scrambled);
        btScrambled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //标记打乱
                if (cube.getCubeState().equals("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB")) {
                    Toast.makeText(getActivity(), "魔方已还原", Toast.LENGTH_SHORT).show();
                } else {
                    cube.markScrambled();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).markScrambled();
                    }
                }
            }
        });
        buidler.setTitle(cube.getName()).setView(view).setNegativeButton(R.string.btn_close, null);
        return buidler.create();
    }

    private void setBatteryImage(int batteryValue) {
        if (batteryValue >= 95) ivBattery.setImageResource(R.drawable.ic_battery_100);
        else if (batteryValue >= 85) ivBattery.setImageResource(R.drawable.ic_battery_90);
        else if (batteryValue >= 75) ivBattery.setImageResource(R.drawable.ic_battery_80);
        else if (batteryValue >= 65) ivBattery.setImageResource(R.drawable.ic_battery_70);
        else if (batteryValue >= 55) ivBattery.setImageResource(R.drawable.ic_battery_60);
        else if (batteryValue >= 45) ivBattery.setImageResource(R.drawable.ic_battery_50);
        else if (batteryValue >= 35) ivBattery.setImageResource(R.drawable.ic_battery_40);
        else if (batteryValue >= 25) ivBattery.setImageResource(R.drawable.ic_battery_30);
        else if (batteryValue >= 15) ivBattery.setImageResource(R.drawable.ic_battery_20);
        else if (batteryValue >= 5) ivBattery.setImageResource(R.drawable.ic_battery_10);
        else ivBattery.setImageResource(R.drawable.ic_battery_10);
    }

    private void setImage() {
        Bitmap bitmap = Utils.drawCubeState(cube.getCubeState());
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
    }
}
