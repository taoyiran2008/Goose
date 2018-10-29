package com.goose.app.ui.personal;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goose.app.GooseApplication;
import com.goose.app.R;
import com.goose.app.configs.Configs;
import com.goose.app.ui.login.LoginActivity;
import com.goose.app.ui.main.MainActivity;
import com.taoyr.app.base.BaseActivity;
import com.taoyr.app.utility.CommonUtils;
import com.taoyr.app.utility.LogMan;
import com.taoyr.app.utility.PictureLoader;
import com.taoyr.app.utility.PictureUtils;
import com.taoyr.widget.widgets.dialog.ActionSheetDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class SignUpPersonalActivity extends BaseActivity<SignUpPersonalContract.Presenter>
        implements SignUpPersonalContract.View, TextWatcher {

    public static final int REQUEST_CODE_TAKE_PICTURE = 1;
    public static final int REQUEST_CODE_PICK_IMAGE = 2;

    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.rb_male)
    RadioButton rb_male;
    @BindView(R.id.rb_female)
    RadioButton rb_female;
    @BindView(R.id.edt_name)
    EditText edt_name;
    @BindView(R.id.txt_next)
    TextView txt_next;

    String mMobile;
    String mPassword;

    Bitmap mBitmap;
    String mNickName;
    String mAvatar;
    String mSex = "NONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_sign_up_personal;
    }

    @Override
    public void handleIntent(Intent intent) {
        mMobile = intent.getStringExtra(Configs.EXTRA_MOBILE_NUMBER);
        mPassword = intent.getStringExtra(Configs.EXTRA_PASSWORD);
    }

    @Override
    protected void initView() {
        setTopBarTitle("个性化");

        // 这个原图外框太小，转换为原型，很大一块内容被截取掉
        //PictureLoader.loadImageViaGlide(img, R.drawable.camera, true, true, R.color.white);
        edt_name.addTextChangedListener(this);
        smartCheckFields();
    }

    @OnClick({R.id.txt_next, R.id.img})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_next:
                mNickName = edt_name.getText().toString();
                if (rb_male.isChecked()) {
                    mSex = "MALE";
                } else if (rb_female.isChecked()) {
                    mSex = "FEMALE";
                }
                //mPresenter.login(mMobile, mPassword);
                // 需要先登陆获取token，才能访问更新接口
                mPresenter.updateProfile(GooseApplication.getInstance().getUserInfo().uid, mNickName, mAvatar);
                break;
            case R.id.img:
                new ActionSheetDialog(mContext).Builder()
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.BULE, new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int witch) {
                                takePictureViaCamera();
                            }
                        }).addSheetItem("相册", ActionSheetDialog.SheetItemColor.BULE, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int witch) {
                        selectPictureFromAlbum();
                    }
                }).show();
                break;
        }
    }

    private void selectPictureFromAlbum() { // getImageFromAlbum
        File outputImageFile = PictureUtils.createTempFile(mContext, "temp.png");
        Uri imageUri = Uri.fromFile(outputImageFile);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        // 调用的是系统图库
        // intent.setType("image");
        // 调用图片选择器
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void takePictureViaCamera() { // getImageFromCamera
        try {
            String state = Environment.getExternalStorageState();
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, REQUEST_CODE_TAKE_PICTURE);
        } catch (SecurityException e) {
            LogMan.logError(e);
            CommonUtils.requestPermission(this,  Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        Bitmap bitmap = null;
        Uri uri = data.getData();
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE:
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                // 一些Android系统中，使用相机拍摄获取照片时，得到的uri=null，这是因为android把
                // 图片封装到了bundle中传递回来
                if (uri == null) {
                    //use bundle to get data
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        bitmap = (Bitmap) bundle.get("data");
                    }
                }
                break;
            default:
                break;
        }

        if (bitmap != null) {
            mBitmap = bitmap;
            mPresenter.uploadHeadImage(bitmap);
        } else {
            processImageUri(uri);
        }
    }

    private void showHeadImage(final Bitmap bitmap) {
        if (bitmap != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    PictureLoader.loadImageViaGlide(img, bitmap, true, true, R.color.white);
                }
            });
        }
    }

    private void processImageUri(final Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO 直接用Gilde加载uri
                try {
                    // 三星5.0手机相册连续选择照片，uri是一个地址，Glide会缓存头像不能更新。
                    // 从uri加载需要在工作线程做。
                    Bitmap bitmap = Glide.with(mContext).load(uri).asBitmap().
                            diskCacheStrategy(DiskCacheStrategy.NONE).into(100, 100).get();
                    mBitmap = bitmap;
                    mPresenter.uploadHeadImage(bitmap);
                    //showHeadImage(bitmap);
                } catch (Exception e) {
                    LogMan.logError("Image load error e: " + e.getMessage());
                }
            }
        }).start();
    }

    private void smartCheckFields() {
        if (!TextUtils.isEmpty(edt_name.getText())) {
            txt_next.setEnabled(true);
            return;
        }
        txt_next.setEnabled(false);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        smartCheckFields();
    }

    @Override
    public void updateProfileOnUi() {
        startActivity(new Intent(mContext, MainActivity.class));
    }

    @Override
    public void loginOnUi() {
        mPresenter.updateProfile(mNickName, mAvatar, mSex);
    }

    @Override
    public void goLogin() {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    @Override
    public void uploadHeadImageOnUi(String url) {
        mAvatar = url;
        // 上传成功后才更新本地头像
        showHeadImage(mBitmap);
    }
}
