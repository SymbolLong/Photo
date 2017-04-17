//
//  ConfigViewController.swift
//  photo-ios
//
//  Created by 张圣龙 on 2017/4/14.
//  Copyright © 2017年 张圣龙. All rights reserved.
//

import UIKit

class ConfigViewController: UIViewController {
    
    @IBOutlet weak var hostField: UITextField!
    @IBOutlet weak var portField: UITextField!
    @IBOutlet weak var idField: UITextField!
    
    var HOST = UserDefaults.standard.string(forKey: Common.HOST_KEY)
    var PORT = UserDefaults.standard.string(forKey: Common.PORT_KEY)
    var ID = UserDefaults.standard.integer(forKey: Common.ID_KEY)
    var fileManager = FileManager()
    var path = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true)[0]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if HOST != nil {
            hostField.text = HOST!
        }
        if PORT != nil {
            portField.text = PORT!
        }
        idField.text = "\(ID)"
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
    
    @IBAction func confirm(_ sender: Any) {
        UserDefaults.standard.setValue(idField.text, forKey: Common.ID_KEY)
        UserDefaults.standard.setValue(hostField.text, forKey: Common.HOST_KEY)
        UserDefaults.standard.setValue(portField.text, forKey: Common.PORT_KEY)
        self.dismiss(animated: true, completion: nil)
    }
    


}
