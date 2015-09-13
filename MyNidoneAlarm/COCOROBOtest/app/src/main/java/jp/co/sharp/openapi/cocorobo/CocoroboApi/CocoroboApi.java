package jp.co.sharp.openapi.cocorobo.CocoroboApi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by matsumotokazuya on 2015/09/13.
 */
public class CocoroboApi {
    private Context ctx;
    public CocoroboApi(Context ctx) { ctx.bindService(new Intent(IOpenApiCocoroboService.class.getName()), mServiceConnection, Context.BIND_AUTO_CREATE);
        this.ctx = ctx;
    }

    public void destroy() {
        if (mService!= null) {
            ctx.unbindService(mServiceConnection);
        }
    }

    public String control(String apiKey, String mode) throws RemoteException {
        return mService.control(apiKey, mode);
    }

    public String getData(String apiKey, String dataKind) throws RemoteException {
        return mService.getData(apiKey, dataKind);
    }

    private IOpenApiCocoroboService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IOpenApiCocoroboService.Stub.asInterface(service);
        }
    };
}