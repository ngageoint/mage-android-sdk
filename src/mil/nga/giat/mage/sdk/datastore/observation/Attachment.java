package mil.nga.giat.mage.sdk.datastore.observation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import mil.nga.giat.mage.sdk.datastore.DBHelper;
import mil.nga.giat.mage.sdk.gson.deserializer.AttachmentDeserializer;
import mil.nga.giat.mage.sdk.http.client.HttpClientManager;
import mil.nga.giat.mage.sdk.utils.MediaUtility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.resize.load.ImageResizer;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "attachments")
public class Attachment implements Parcelable {

	@DatabaseField(generatedId = true, columnName="pk_id")
	private Long id;

	@DatabaseField(unique = true, columnName="remote_id")
	private String remoteId;

	@DatabaseField(columnName="content_type")
	private String contentType;

	@DatabaseField(columnName="size")
	private Long size;

	@DatabaseField(columnName="name")
	private String name;

	@DatabaseField(columnName="local_path")
	private String localPath;

	@DatabaseField(columnName="remote_path")
	private String remotePath;
	
	@DatabaseField(columnName="url")
	private String url;

	@DatabaseField(foreign = true)
	private Observation observation;

	public Attachment() {
		// ORMLite needs a no-arg constructor
	}

	public Attachment(String contentType, Long size, String name, String localPath, String remotePath) {
		this.contentType = contentType;
		this.size = size;
		this.name = name;
		this.localPath = localPath;
		this.remotePath = remotePath;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(String remoteId) {
		this.remoteId = remoteId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	@Override
	public String toString() {
		return "Attachment [pk_id=" + id + ", content_type=" + contentType + ", size=" + size + ", name=" + name + ", local_path=" + localPath + ", remote_path=" + remotePath + ", remote_id=" + remoteId + ", url=" + url + ", observation=" + observation.getId() + "]";
	}
	
	// Parcelable stuff
	public Attachment(Parcel in) {
		id = (Long)in.readValue(Long.class.getClassLoader());
		remoteId = in.readString();
		contentType = in.readString();
		size = (Long)in.readValue(Long.class.getClassLoader());
		name = in.readString();
		localPath = in.readString();
		remotePath = in.readString();
		url = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeValue(id);
		out.writeString(remoteId);
		out.writeString(contentType);
		out.writeValue(size);
		out.writeString(name);
		out.writeString(localPath);
		out.writeString(remotePath);
		out.writeString(url);
	}
	
	public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {
	      public Attachment createFromParcel(Parcel source) {
	            return new Attachment(source);
	      }
	      public Attachment[] newArray(int size) {
	            return new Attachment[size];
	      }
	};
	
	public void stageForUpload(Context c) {
		try {
			File stageDir = MediaUtility.getMediaStageDirectory();
			File stagedFile = new File(stageDir, new File(localPath).getName());
		    OutputStream out = new FileOutputStream(stagedFile);
		    
		    // XXX problem with this is that other exif data is lost
		    // we either need to grab all the fields and re-write them or this should happen on the server (probably better)
		    // Only reason we are doing this is because then we guarantee images are oriented properly on all devices
			Bitmap rotated = ImageResizer.orientImage(localPath, BitmapFactory.decodeFile(localPath));
			rotated.compress(CompressFormat.JPEG, 100, out);
			
		    out.close();
		    setLocalPath(stagedFile.getAbsolutePath());
		    DBHelper.getInstance(c).getAttachmentDao().update(this);
		    rotated.recycle();
		} catch (Exception e) {
			Log.e("Attachment", "Unable to stage for upload", e);
		}
	}
	
	public void saveToServer(Context c) {
		stageForUpload(c);
		Observation o = getObservation();
		DefaultHttpClient httpClient = HttpClientManager.getInstance(c).getHttpClient();	
		try {
			URI endpointUri = new URL(o.getUrl() + "/attachments").toURI();	
			HttpPost request = new HttpPost(endpointUri);
			String mimeType = MediaUtility.getMimeType(getLocalPath());

			FileBody fileBody = new FileBody(new File(getLocalPath()));
			FormBodyPart fbp = new FormBodyPart("attachment", fileBody);
			fbp.addField("Content-Type", mimeType);

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart(fbp);

			request.setEntity(reqEntity);

			HttpResponse response = httpClient.execute(request);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String json = EntityUtils.toString(resEntity);
				Attachment a = AttachmentDeserializer.getGsonBuilder().fromJson(json, Attachment.class);
				this.setContentType(a.getContentType());
				this.setName(a.getName());
				this.setRemoteId(a.getRemoteId());
				this.setRemotePath(a.getRemotePath());
				this.setSize(a.getSize());
				this.setUrl(a.getUrl());
				
				// TODO go save this attachment again
				DBHelper.getInstance(c).getAttachmentDao().update(this);
			}

		} catch (Exception e) {
			Log.e("Attachment", "Error posting attachment " + getLocalPath(), e);
		}
	}

}
