package algorithm.basic03;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Created by mood321
 * @Date 2019/10/9 0009
 * @Description 猫狗队列问题
 */
public class Code_04_DogCatQueue {

    static class Pet {
        private String type;

        public Pet(String type) {
            this.type = type;
        }

        public String getPetType() {
            return this.type;
        }
    }

    public static class Dog extends Pet {
        public Dog() {
            super("dog");
        }
    }

    /**
     * Cat 的实体对象
     */
    public static class Cat extends Pet {
        public Cat() {
            super("cat");
        }
    }

    public static class PetEnterQueue {
        public Pet pet;
        public Integer count;

        public PetEnterQueue(Pet pet, Integer count) {
            this.count = count;
            this.pet = pet;
        }
    }

    public static class DogCatQueue {
        private Queue<PetEnterQueue> dogQ;
        private Queue<PetEnterQueue> catQ;
        private Integer count;

        public DogCatQueue() {
            dogQ = new LinkedList<PetEnterQueue>();
            catQ = new LinkedList<PetEnterQueue>();
            count = 0;
        }

        public void add(Pet pet) {
            if ("dog".equals(pet.type)) {
                dogQ.add(new PetEnterQueue(pet, count++));
            }
            if ("cat".equals(pet.type)) {
                catQ.add(new PetEnterQueue(pet, count++));
            } else {
                throw new IllegalArgumentException("err, not dog or cat");
            }
        }

        public Dog pollDog() {
            if (!this.isDogQueueEmpty()) {
                return (Dog) this.dogQ.poll().pet;
            } else {
                throw new RuntimeException("Dog queue is empty!");
            }
        }

        public Cat pollCat() {
            if (!this.isCatQueueEmpty()) {
                return (Cat) this.catQ.poll().pet;
            } else
                throw new RuntimeException("Cat queue is empty!");
        }

        public boolean isEmpty() {
            return this.dogQ.isEmpty() && this.catQ.isEmpty();
        }

        public boolean isDogQueueEmpty() {
            return this.dogQ.isEmpty();
        }

        public boolean isCatQueueEmpty() {
            return this.catQ.isEmpty();
        }

        public Pet pollAll() {
            if(!catQ.isEmpty()&& !dogQ.isEmpty()){
                if (this.dogQ.peek().count < this.catQ.peek().count) {
                    return this.dogQ.poll().pet;
                } else {
                    return this.catQ.poll().pet;
                }
            } else if (!this.dogQ.isEmpty()) {
                return this.dogQ.poll().pet;
            } else if (!this.catQ.isEmpty()) {
                return this.catQ.poll().pet;
            } else {
                throw new RuntimeException("err, queue is empty!");
            }
        }
    }

}
