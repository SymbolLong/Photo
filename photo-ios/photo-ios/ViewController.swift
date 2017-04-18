//
//  ViewController.swift
//  photo-ios
//
//  Created by 张圣龙 on 2017/4/13.
//  Copyright © 2017年 张圣龙. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var imageView: UIImageView!
    var requestAddress = ""
    var timer: Timer?
    var interval: TimeInterval = 5
    var id = 0
    var fileManager = FileManager()
    var path = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0]

    override func viewDidLoad() {
        super.viewDidLoad()
        UIApplication.shared.isIdleTimerDisabled = true//屏幕保持常亮
        addGesture()//添加手势
        //setTimer(interval: interval)//使用定时器
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        initContent()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        UserDefaults.standard.set(id, forKey: "id")
    }
    
    //隐藏状态栏
    override var prefersStatusBarHidden: Bool{
        return true;
    }
    
    //初始化
    func initContent(){
        id = UserDefaults.standard.integer(forKey: Common.ID_KEY)
        let host = UserDefaults.standard.string(forKey: Common.HOST_KEY) ?? "http://localhost"
        let port = UserDefaults.standard.string(forKey: Common.PORT_KEY) ?? "8888"
        requestAddress = "\(host):\(port)\(Common.API_GET)"
        
        let name = "timg-\(id)"
        if let data = NSData(contentsOfFile:"\(path)/\(name)"){
            imageView.image = UIImage(data: data as Data)
        } else {
            imageView.image = UIImage(named: "fu")
        }
        let loadURL = URL(string: requestAddress+"\(id+1)")!
        downloadImage(manager: fileManager,path: "\(path)/timg-\(id+1)",url: loadURL)//加载下一张
        sleep(3)//给网络一点时间
    }
    
    //添加手势
    func addGesture(){
        //上
        let faster = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        faster.direction = .up
        self.view.addGestureRecognizer(faster)
        //下
        let slower = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        slower.direction = .down
        self.view.addGestureRecognizer(slower)
        //左
        let pre = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        pre.direction = .right
        self.view.addGestureRecognizer(pre)
        //右
        let next = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        next.direction = .left
        self.view.addGestureRecognizer(next)
    }
    
    //加速||减速播放，上一张||下一张
    func swipeGesture(sender: UISwipeGestureRecognizer){
        switch sender.direction {
        case UISwipeGestureRecognizerDirection.up:
            interval = interval - 1 < 1 ? 1 : interval - 1
            setTimer(interval: interval)
        case UISwipeGestureRecognizerDirection.down:
            interval = interval + 1 > 10 ? 10 : interval + 1
            setTimer(interval: interval)
        case UISwipeGestureRecognizerDirection.left:
            setTimer(interval: 0)
            nextImage()
        case UISwipeGestureRecognizerDirection.right:
            setTimer(interval: 0)
            preImage()
        default:
            print("error,theoretically no code will access here")
        }
    }
    
    func setTimer(interval: TimeInterval){
        if timer != nil {
            timer!.invalidate()
        }
        if  interval > 0 {
            timer = Timer.scheduledTimer(timeInterval: interval, target: self, selector: #selector(self.nextImage), userInfo: nil, repeats: true)
        }
    }
    
    func preImage(){
        print("preImage\(id)")
        id = id <= 0 ? 1 : id - 1
        let name = "timg-\(id)"
        let data = NSData(contentsOfFile:"\(path)/\(name)")!
        imageView.image = UIImage(data: data as Data)
    }
    
    func nextImage(){
        print("next\(id)")
        id += 1
        let name = "timg-\(id)"
        if let data = NSData(contentsOfFile:"\(path)/\(name)"){
            imageView.image = UIImage(data: data as Data)
        } else {
            let nextId = id + 1;
            let loadURL = URL(string: requestAddress+"\(nextId)")!
            downloadImage(manager: fileManager,path: "\(path)/timg-\(nextId)",url: loadURL)
        }
    }
    
    private func downloadImage(manager: FileManager,path: String,url: URL){
        // 使用网络请求异步加载data并保存
        let session = URLSession.shared
        let task = session.dataTask(with: url, completionHandler: { (data, response, error) in
            if error != nil{
                DispatchQueue.main.async(execute: {
                    let alert = UIAlertController(title: "出错了", message: "服务器暂时不可用", preferredStyle: UIAlertControllerStyle.alert)
                    let okAction = UIAlertAction(title: "去配置", style: .default, handler: { (action) in
                        let controller = self.storyboard?.instantiateViewController(withIdentifier: "config")
                        //let controller = ConfigViewController()
                        self.present(controller!, animated: true,completion: nil)
                    })
                    let cancelAction = UIAlertAction(title: "取消", style: .cancel, handler: nil)
                    alert.addAction(cancelAction)
                    alert.addAction(okAction)
                    self.present(alert, animated: true,completion: nil)
                })
            }else{
                //保存图片
                manager.createFile(atPath: path, contents: data!, attributes: nil)
            }
        })
        task.resume()
    }

}

