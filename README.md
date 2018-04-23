# SampleDownloader
[ ![Download](https://api.bintray.com/packages/bytehit/maven/Downloader/images/download.svg) ](https://bintray.com/bytehit/maven/Downloader/_latestVersion)


> 一个简单的文件下载库，对系统下载服务进行的二次简单封装，支持多任务同时下载，使用系统下载服务，不用担心应用进程被杀掉后终止下载的问题。不支持断点续传。


## Usage

**Step1:添加依赖** 

	dependencies {
	    compile 'com.leo618:downloader:0.0.3'
	}



**Step2:创建下载任务：** 创建下载任务并开启下载，支持多个任务同时进行


	Downloader.Task downloadTask = new Downloader.Task()
	        .setDownloadUrl(url)            //下载链接
	        .setDownloadFilePath(filePath)  //文件全路径
	        .setDownloadCallback(mCallback) //下载回调
	        //以上三个参数必须设置
	        .notDeleteExist()               //不删除已有旧文件
	        .setNotificationVisibility(Downloader.NOTIFICATION_HIDDEN)//通知栏通知隐藏
	        .setTitle("下载标题")            //通知栏显示通知的标题
	        .setDescription("下载描述内容")  //通知栏显示通知的描述
	        .setAllowScan(true)             //允许被系统外部扫描到
	        .showInDownloadsUi();           //在系统下载列表中显示
	Downloader.getInstance(getApplicationContext()).download(downloadTask);
    mDownloadId = downloadTask.getDownloadId();


**Step3:取消下载：** 可以根据下载任务的id取消下载，取消下载后会将临时文件删除，下载完成的文件取消下载也会被删除

    Downloader.getInstance(getApplicationContext()).cancel(mDownloadId);



----------

**PS  说明两点：** 

1.应用各自已经完成了对外置存储读写权限的申请处理；

2.如需不在通知栏显示需要先在清单文件中加入android.permission.DOWNLOAD_WITHOUT_NOTIFICATION权限(此权限不用动态申请)，然后在创建task时调用setNotificationVisibility(Downloader.NOTIFICATION_VISIBLE)
