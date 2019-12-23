package algorithm.Interview.practice01;

import lombok.Data;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author mood321
 * @date 2019/12/23 21:44
 * @email 371428187@qq.com
 */
public class P_1_3_CatDogQueue {

    @Data
    public static  class Pet{
        private  String type;
        public Pet(String type) {
            this.type=type;
        }
    }
    @Data
    public  static  class  Cat extends  Pet{

        public Cat(String type) {
            super("cat");
        }
    }
    @Data
    public  static  class  Dog extends  Pet{

        public Dog(String type) {
            super("dog");
        }
    }

    // 队列中存放的对象
    @Data
    public static  class DogCat{
        private Pet pet;
        private Integer count;

        public DogCat(Pet pet, Integer count) {
            this.pet = pet;
            this.count = count;
        }
    }
    // 队列
     public static  class  DogCatQueue{
        private Queue<DogCat> catQueue;
        private Queue<DogCat> dogQueue;
        private  Integer count;

        public DogCatQueue(Queue<DogCat> catQueue, Queue<DogCat> dogQueue) {
            this.catQueue = new LinkedList<>();
            this.dogQueue =new LinkedList<>();
            this.count = 0;
        }

        public void add (Pet pet){
            if("dog".equals(pet.type)){
                dogQueue.add(new DogCat(pet,count++)) ;
            }else  if("cat".equals(pet.type)){
                dogQueue.add(new DogCat(pet,count++)) ;
            } else {
                throw new  RuntimeException("Your type is error");
            }
        }
        public  Pet pollAll(){
            if(!dogQueue.isEmpty() && !catQueue.isEmpty()) {    // 
                if(dogQueue.peek().count>catQueue.peek().count){
                    return  catQueue.poll().pet;
                }  else {
                    return  dogQueue.poll().pet;
                }
            } else if(!dogQueue.isEmpty())    {
                return  dogQueue.poll().pet;
            }   else if(!catQueue.isEmpty())    {
                return  catQueue.poll().pet;
            } else {
                throw new  RuntimeException("Your Queue is empty" );
            }
        }

        public Pet pollCat(){
            if(catQueue.isEmpty()){
                throw new  RuntimeException("Your Queue is empty" );
            }
            return  catQueue.poll().pet;
        }
        public Pet pollDog(){
            if(dogQueue.isEmpty()){
                throw new  RuntimeException("Your Queue is empty" );
            }
            return  dogQueue.poll().pet;
        }
        public boolean dogIsEmpty(){
             return  dogQueue.isEmpty();
        }
        public boolean catIsEmpty(){
             return  catQueue.isEmpty();
        }
    }
}