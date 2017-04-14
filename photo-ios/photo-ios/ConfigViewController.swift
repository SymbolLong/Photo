//
//  ConfigViewController.swift
//  photo-ios
//
//  Created by 张圣龙 on 2017/4/14.
//  Copyright © 2017年 张圣龙. All rights reserved.
//

import UIKit

class ConfigViewController: UIViewController {
    
    var HOST = UserDefaults.standard.string(forKey: Common.HOST_KEY)
    var id = UserDefaults.standard.integer(forKey: Common.ID_KEY)
    var fileManager = FileManager()
    var path = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("配置开始")
    }
    
    private func downloadImage(manager: FileManager,path: String,url: URL){
        // 使用网络请求异步加载data并保存
        let session = URLSession.shared
        let task = session.dataTask(with: url, completionHandler: { (data, response, error) in
            if error != nil{
                
            }else{
                //保存图片
                manager.createFile(atPath: path, contents: data!, attributes: nil)
            }
        })
        task.resume()
    }


}
