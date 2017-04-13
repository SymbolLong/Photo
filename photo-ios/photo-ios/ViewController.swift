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
    let host = "http://localhost:8888/api/get?id="
    var timer: Timer?
    var interval: TimeInterval = 5
    var id = UserDefaults.standard.integer(forKey: "id")
    var fileManager = FileManager()
    var path = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0]

    override func viewDidLoad() {
        super.viewDidLoad()
        //屏幕保持常亮
        UIApplication.shared.isIdleTimerDisabled = true
        //添加手势
        addGesture()
        //使用定时器
        setTimer(interval: interval)
        //初始化id
        if id == 0 {
            let loadURL = URL(string: host+"\(1)")!
            downloadImage(manager: fileManager,path: "\(path)/timg-\(1)",url: loadURL)
            sleep(3)
            nextImage()
        }
    }
    
    //隐藏状态栏
    override var prefersStatusBarHidden: Bool{
        return true;
    }
    
    //添加手势
    func addGesture(){
        //上下左右
        let faster = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        faster.direction = .up
        self.view.addGestureRecognizer(faster)
        
        let slower = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        slower.direction = .down
        self.view.addGestureRecognizer(slower)
        
        let pre = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        pre.direction = .right
        self.view.addGestureRecognizer(pre)
        
        let next = UISwipeGestureRecognizer(target: self, action: #selector(self.swipeGesture(sender:)))
        next.direction = .left
        self.view.addGestureRecognizer(next)
    }
    
    //加速||减速播放，上一张||下一张
    func swipeGesture(sender: UISwipeGestureRecognizer){
        switch sender.direction {
        case UISwipeGestureRecognizerDirection.up:
            interval -= 1
            if interval < 1 {
                interval = 1
            }
            setTimer(interval: interval)
        case UISwipeGestureRecognizerDirection.down:
            interval += 1
            if interval > 10 {
                interval = 10
            }
            setTimer(interval: interval)
        case UISwipeGestureRecognizerDirection.left:
            setTimer(interval: 0)
            nextImage()
        case UISwipeGestureRecognizerDirection.right:
            setTimer(interval: 0)
            preImage()
        default:
            print("error")
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
        id -= 1
        if id <= 0 {
            id = 1
        }
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
            let loadURL = URL(string: host+"\(nextId)")!
            downloadImage(manager: fileManager,path: "\(path)/timg-\(nextId)",url: loadURL)
        }
        UserDefaults.standard.set(id, forKey: "id")
    }
    
    private func downloadImage(manager: FileManager,path: String,url: URL){
        // 使用网络请求异步加载data并保存
        let session = URLSession.shared
        let task = session.dataTask(with: url, completionHandler: { (data, response, error) in
            if error != nil{
                DispatchQueue.main.async(execute: {
                    let alert = UIAlertController(title: "提示", message: "服务器维护中....", preferredStyle: UIAlertControllerStyle.alert)
                    alert.addAction(UIAlertAction(title: "坐等恢复", style: UIAlertActionStyle.default, handler: nil))
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

