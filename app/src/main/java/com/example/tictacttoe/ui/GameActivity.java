package com.example.tictacttoe.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tictacttoe.MainActivity;
import com.example.tictacttoe.PreferencesService;
import com.example.tictacttoe.R;
import com.example.tictacttoe.TicTacToe;
import com.example.tictacttoe.TicTacToeView;
import static android.view.View.GONE;

public class GameActivity extends AppCompatActivity implements TicTacToe.TicTacToeListener, TicTacToeView.SquarePressedListener {
    Button start, resetButton, cancel;
    TicTacToe ticTacToe;
    int x = 0;
    TextView information;
    TicTacToeView ticTacToeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        PreferencesService.init(this);

        ticTacToe = new TicTacToe();

        ticTacToeView = findViewById(R.id.ticTacToeView);

        ticTacToe.setTicTacToeListener(this);
        ticTacToeView.setSquarePressListener(this);

        start = findViewById(R.id.start);
        resetButton = findViewById(R.id.resetButton);
        cancel = (Button) findViewById(R.id.cancel);
        information = (TextView) findViewById(R.id.information);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GameActivity.this.finish();
                startActivity(new Intent(GameActivity.this, GameActivity.class));

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GameActivity.this, MainActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        information.setText(" First is ");

        information.append(PreferencesService.instance().getFirstPlayer());
        information.append("\'s turn");

    }

    private void resetGameUi() {
        ticTacToeView.reset();

        ticTacToeView.setEnabled(true);

        information.setVisibility(GONE);
        resetButton.setVisibility(GONE);

    }

    @Override
    public void gameWonBy(TicTacToe.BoardPlayer boardPlayer, TicTacToe.SquareCoordinates[] winPoints) {

        information.setVisibility(View.VISIBLE);

        if (ticTacToe.getPlayerToMove().move == TicTacToe.BoardState.MOVE_X) {
            information.setText("Winner is " + PreferencesService.instance().getFirstPlayer());

            sendSMS(PreferencesService.instance().getFirstNum(), "Winner is " + PreferencesService.instance().getFirstPlayer());
            sendSMS(PreferencesService.instance().getSecondNum(), "Winner is " + PreferencesService.instance().getFirstPlayer());
        } else {
            information.setText("Winner is " + PreferencesService.instance().getSecondPlayer());

            sendSMS(PreferencesService.instance().getFirstNum(), "Winner is " + PreferencesService.instance().getSecondPlayer());
            sendSMS(PreferencesService.instance().getSecondNum(), "Winner is " + PreferencesService.instance().getSecondPlayer());
        }

        ticTacToeView.animateWin(winPoints[0].i, winPoints[0].j, winPoints[2].i, winPoints[2].j);
        ticTacToeView.setEnabled(false);
        resetButton.setVisibility(View.VISIBLE);
        start.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);

    }

    @Override
    public void gameEndsWithATie() {

        information.setVisibility(View.VISIBLE);
        information.setText(R.string.game_ends_draw);
        resetButton.setVisibility(View.VISIBLE);
        ticTacToeView.setEnabled(false);

    }

    @Override
    public void movedAt(int x, int y, int move) {

        if (ticTacToe.getPlayerToMove().move == TicTacToe.BoardState.MOVE_X) {
            information.setText(PreferencesService.instance().getSecondPlayer() + "\' turn");
        } else {
            information.setText(PreferencesService.instance().getFirstPlayer() + "\' turn");
        }

        if (move == TicTacToe.BoardState.MOVE_X) {
            ticTacToeView.drawXAtPosition(x, y);
        } else {
            ticTacToeView.drawOAtPosition(x, y);
        }
    }

    @Override
    public void onSquarePressed(int i, int j) {
        ticTacToe.moveAt(i, j);
    }

    @Override
    public void onBackPressed() {

        if (x == 0) {
            x = 1;
            Toast.makeText(this, "One More click to exit!!", Toast.LENGTH_SHORT).show();

            if (x == 1) {
                exitAppCode();
            }
        }
    }

    public void exitAppCode() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

         registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}