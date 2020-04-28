<h2> 编译原理之美学习笔记

##  01 原理篇
### 01 | 理解代码：编译器的前端技术
<p> 这里的“前端”指的是编译器对程序代码的分析和理解过程。它通常只跟语言的语法有关，跟目标机器无关。而与之对应的“后端”则是生成目标代码的过程，跟目标机器有关。为了方便你理解，我用一张图直观地展现了编译器的整个编译过程。

<img src="https://static001.geekbang.org/resource/image/06/93/06b80f8484f4d88c6510213eb27f2093.jpg" >
<p>编译器的“前端”技术分为词法分析、语法分析和语义分析三个部分。而它主要涉及自动机和形式语言方面的基础的计算理论。
<h3> 词法分析

<p> 通常，编译器的第一项工作叫做词法分析。就像阅读文章一样，文章是由一个个的中文单词组成的。程序处理也一样，只不过这里不叫单词，而是叫做“词法记号”，英文叫 Token。我嫌“词法记号”这个词太长，后面直接将它称作 Token 吧。
<p>我们可以通过制定一些规则来区分每个不同的 Token，我举了几个例子，你可以看一下。

<li>识别 age 这样的标识符。它以字母开头，后面可以是字母或数字，直到遇到第一个既不是字母又不是数字的字符时结束。
<li>识别 >= 这样的操作符。 当扫描到一个 > 字符的时候，就要注意，它可能是一个 GT（Greater Than，大于）操作符。但由于 GE（Greater Equal，大于等于）也是以 > 开头的，所以再往下再看一位，如果是 =，那么这个 Token 就是 GE，否则就是 GT。
<li>识别 45 这样的数字字面量。当扫描到一个数字字符的时候，就开始把它看做数字，直到遇到非数字的字符。
<p>它分析整个程序的字符串，当遇到不同的字符时，会驱使它迁移到不同的状态。例如，词法分析程序在扫描 age 的时候，处于“标识符”状态，等它遇到一个 > 符号，就切换到“比较操作符”的状态。词法分析过程，就是这样一个个状态迁移的过程。
  <img  src="https://static001.geekbang.org/resource/image/6d/7e/6d78396e6426d0ad5c5230203d17da7e.jpg" >
  
 <h3>语法分析
 
<p> 编译器下一个阶段的工作是语法分析。词法分析是识别一个个的单词，而语法分析就是在词法分析的基础上识别出程序的语法结构。这个结构是一个树状结构，是计算机容易理解和执行的
<img src="https://static001.geekbang.org/resource/image/93/fb/9380037e2d2c2c2a8ff50f1367ff37fb.jpg" >
<p>程序也有定义良好的语法结构，它的语法分析过程，就是构造这么一棵树。一个程序就是一棵树，这棵树叫做抽象语法树（Abstract Syntax Tree，AST）。树的每个节点（子树）是一个语法单元，这个单元的构成规则就叫“语法”。每个节点还可以有下级节点

<p>一种非常直观的构造思路是自上而下进行分析。首先构造根节点，代表整个程序，之后向下扫描 Token 串，构建它的子节点。当它看到一个 int 类型的 Token 时，知道这儿遇到了一个变量声明语句，于是建立一个“变量声明”节点；接着遇到 age，建立一个子节点，这是第一个变量；之后遇到 =，意味着这个变量有初始化值，那么建立一个初始化的子节点；最后，遇到“字面量”，其值是 45。
<p>
<p>这样，一棵子树就扫描完毕了。程序退回到根节点，开始构建根节点的第二个子节点。这样递归地扫描，直到构建起一棵完整的树。
<img  src="https://static001.geekbang.org/resource/image/cb/16/cbf2b953cb84ef30b154470804262c16.jpg">
<p>  递归下降算法是一种自顶向下的算法，与之对应的，还有自底向上的算法。这个算法会先将最下面的叶子节点识别出来，然后再组装上一级节点。有点儿像搭积木，我们总是先构造出小的单元，然后再组装成更大的单元。原理就是这么简单。

<h3> 语义分析

<p>编译器接下来做的工作是语义分析。说白了，语义分析就是要让计算机理解我们的真实意图，把一些模棱两可的地方消除掉。

<p>你可能会觉得理解自然语言的含义已经很难了，所以计算机语言的语义分析也一定很难。其实语义分析没那么复杂，因为计算机语言的语义一般可以表达为一些规则，你只要检查是否符合这些规则就行了。比如：

<li>某个表达式的计算结果是什么数据类型？如果有数据类型不匹配的情况，是否要做自动转换？
<li>如果在一个代码块的内部和外部有相同名称的变量，我在执行的时候到底用哪个？ 就像“我喜欢又聪明又勇敢的你”中的“你”，到底指的是谁，需要明确。
<li>在同一个作用域内，不允许有两个名称相同的变量，这是唯一性检查。你不能刚声明一个变量 a，紧接着又声明同样名称的一个变量 a，这就不允许了。

<p>语义分析基本上就是做这样的事情，也就是根据语义规则进行分析判断。
<p>
<p>语义分析工作的某些成果，会作为属性标注在抽象语法树上，比如在 age 这个标识符节点和 45 这个字面量节点上，都会标识它的数据类型是 int 型的。
<p>在这个树上还可以标记很多属性，有些属性是在之前的两个阶段就被标注上了，比如所处的源代码行号，这一行的第几个字符。这样，在编译程序报错的时候，就可以比较清楚地了解出错的位置。
<p>做了这些属性标注以后，编译器在后面就可以依据这些信息生成目标代码了，我们在编译技术的后端部分会去讲。

### 02 | 正则文法和有限自动机：纯手工打造词法分析器
<p>标识符、比较操作符和数字字面量这三种 Token 的词法规则。

<li>标识符：第一个字符必须是字母，后面的字符可以是字母或数字。
<li>比较操作符：> 和 >=（其他比较操作符暂时忽略）。
<li>数字字面量：全部由数字构成（像带小数点的浮点数，暂时不管它）。

<p>我们就是依据这样的规则，来构造有限自动机的。这样，词法分析程序在遇到 age、>= 和 45 时，会分别识别成标识符、比较操作符和数字字面量。不过上面的图只是一个简化的示意图，一个严格意义上的有限自动机是下面这种画法：
<img  src="https://static001.geekbang.org/resource/image/15/35/15da400d09ede2ce6ac60fa6d5342835.jpg" >
我来解释一下上图的 5 种状态。

1. 初始状态：刚开始启动词法分析的时候，程序所处的状态。

2. 标识符状态：在初始状态时，当第一个字符是字母的时候，迁移到状态 2。当后续字符是字母和数字时，保留在状态 2。如果不是，就离开状态 2，写下该 Token，回到初始状态。

3. 大于操作符（GT）：在初始状态时，当第一个字符是 > 时，进入这个状态。它是比较操作符的一种情况。

4. 大于等于操作符（GE）：如果状态 3 的下一个字符是 =，就进入状态 4，变成 >=。它也是比较操作符的一种情况。

5. 数字字面量：在初始状态时，下一个字符是数字，进入这个状态。如果后续仍是数字，就保持在状态 5。

<h3> 初识正则表达式

<p>需要注意的是，不同语言的标识符、整型字面量的规则可能是不同的。比如，有的语言可以允许用 Unicode 作为标识符，也就是说变量名称可以是中文的。还有的语言规定，十进制数字字面量的第一位不能是 0。这时候正则表达式会有不同的写法，对应的有限自动机自然也不同。而且，不同工具的正则表达式写法会略有不同，但大致是差不多的
<p>解析 int age = 40，处理标识符和关键字规则的冲突
<p>说完正则表达式，我们接着去处理其他词法，比如解析“int age = 40”这个语句，以这个语句为例研究一下词法分析中会遇到的问题：多个规则之间的冲突

<h3> 在这里，我们来回顾一下：什么是关键字？
<p>关键字是语言设计中作为语法要素的词汇，例如表示数据类型的 int、char，表示程序结构的 while、if，表述特殊数据取值的 null、NAN 等。
<p>
<p>除了关键字，还有一些词汇叫保留字。保留字在当前的语言设计中还没用到，但是保留下来，因为将来会用到。我们命名自己的变量、类名称，不可以用到跟关键字和保留字相同的字符串。那么我们在词法分析器中，如何把关键字和保留字跟标识符区分开呢？
<p>
<p>以“int age = 40”为例，我们把有限自动机修改成下面的样子，借此解决关键字和标识符的冲突。
<img  src="https://static001.geekbang.org/resource/image/52/b4/52afab1d7c30b04c91b2b0a018dcc9b4.jpg" >

<p>这个思路其实很简单。在识别普通的标识符之前，你先看看它是关键字还是保留字就可以了。具体做法是：

>当第一个字符是 i 的时候，我们让它进入一个特殊的状态。接下来，如果它遇到 n 和 t，就进入状态 4。但这还没有结束，如果后续的字符还有其他的字母和数字，它又变成了普通的标识符。比如，我们可以声明一个 intA（int 和 A 是连着的）这样的变量，而不会跟 int 关键字冲突。

<p>解析算术表达式
<p>解析完“int age = 40”之后，我们再按照上面的方法增加一些规则，这样就能处理算术表达式，例如“2+3*5”。 增加的词法规则如下：
<pre>
    Plus :  '+'
    Minus : '-'
    Star :  '*' 
    Slash : '/'
</pre>

### 03 | 语法分析（一）：纯手工打造公式计算器
<p>语法分析的原理和递归下降算法（Recursive Descent Parsing），并初步了解上下文无关文法（Context-free Grammar，CFG）。

<p>“int age = 45”这个语句，画了一个语法分析算法的示意图：
<img  src="https://static001.geekbang.org/resource/image/cb/16/cbf2b953cb84ef30b154470804262c16.jpg" >
<p>我们首先把变量声明语句的规则，用形式化的方法表达一下。它的左边是一个非终结符（Non-terminal）。右边是它的产生式（Production Rule）。在语法解析的过程中，左边会被右边替代。如果替代之后还有非终结符，那么继续这个替代过程，直到最后全部都是终结符（Terminal），也就是 Token。只有终结符才可以成为 AST 的叶子节点。这个过程，也叫做推导（Derivation）过程：
<p>你可以看到，int 类型变量的声明，需要有一个 Int 型的 Token，加一个变量标识符，后面跟一个可选的赋值表达式。我们把上面的文法翻译成程序语句，伪代码如下：
<pre>
// 伪代码
MatchIntDeclare(){
  MatchToken(Int)；        // 匹配 Int 关键字
  MatchIdentifier();       // 匹配标识符
  MatchToken(equal);       // 匹配等号
  MatchExpression();       // 匹配表达式
} </pre>
实际代码在 SimpleCalculator.java 类的 IntDeclare() 方法中：
<pre>
SimpleASTNode node = null;
Token token = tokens.peek();    // 预读
if (token != null && token.getType() == TokenType.Int) {   // 匹配 Int
    token = tokens.read();      // 消耗掉 int
    if (tokens.peek().getType() == TokenType.Identifier) { // 匹配标识符
        token = tokens.read();  // 消耗掉标识符
        // 创建当前节点，并把变量名记到 AST 节点的文本值中，
        // 这里新建一个变量子节点也是可以的
        node = new SimpleASTNode(ASTNodeType.IntDeclaration, token.getText());
        token = tokens.peek();  // 预读
        if (token != null && token.getType() == TokenType.Assignment) {
            tokens.read();      // 消耗掉等号
            SimpleASTNode child = additive(tokens);  // 匹配一个表达式
            if (child == null) {
                throw new Exception("invalide variable initialization, expecting an expression");
            }
            else{
                node.addChild(child);
            }
        }
    } else {
        throw new Exception("variable name expected");
    }
}  </pre>
<p> 解析变量声明语句时，我先看第一个 Token 是不是 int。如果是，那我创建一个 AST 节点，记下 int 后面的变量名称，然后再看后面是不是跟了初始化部分，也就是等号加一个表达式。我们检查一下有没有等号，有的话，接着再匹配一个表达式。
<p> 逻辑就是是算法题里面,字符串做运算那个(用栈实现),但多了关键字
<p>在这个过程中，上级文法嵌套下级文法，上级的算法调用下级的算法。表现在生成 AST 中，上级算法生成上级节点，下级算法生成下级节点。这就是“下降”的含义。
<p>
<p>分析上面的伪代码和程序语句，你可以看到这样的特点：程序结构基本上是跟文法规则同构的。这就是递归下降算法的优点，非常直观。

<h3> 用上下文无关文法描述算术表达式
<p>算术表达式要包含加法和乘法两种运算（简单起见，我们把减法与加法等同看待，把除法也跟乘法等同看待），加法和乘法运算有不同的优先级。我们的规则要能匹配各种可能的算术表达式：
 <p> 在字符串计算的时候 乘法的 优先级是要高的
 <p>这种文法已经没有办法改写成正则文法了，它比正则文法的表达能力更强，叫做“上下文无关文法”。正则文法是上下文无关文法的一个子集。它们的区别呢，就是上下文无关文法允许递归调用，而正则文法不允许
<p>上下文无关的意思是，无论在任何情况下，文法的推导规则都是一样的。比如，在变量声明语句中可能要用到一个算术表达式来做变量初始化，而在其他地方可能也会用到算术表达式。不管在什么地方，算术表达式的语法都一样，都允许用加法和乘法，计算优先级也不变。好在你见到的大多数计算机语言，都能用上下文无关文法来表达它的语法。

<h3>解析算术表达式：理解“递归”的含义

<p>“additiveExpression Plus multiplicativeExpression”这个文法规则的第一部分就递归地引用了自身，这种情况叫做左递归。通过上面的分析，我们知道左递归是递归下降算法无法处理的，这是递归下降算法最大的问题。
<p>我们先尝试能否匹配乘法表达式，如果不能，那么这个节点肯定不是加法节点，因为加法表达式的两个产生式都必须首先匹配乘法表达式。遇到这种情况，返回 null 就可以了，调用者就这次匹配没有成功。如果乘法表达式匹配成功，那就再尝试匹配加号右边的部分，也就是去递归地匹配加法表达式。如果匹配成功，就构造一个加法的 ASTNode 返回。
<p>
<p>问题是什么呢？计算顺序发生错误了。连续相加的表达式要从左向右计算，这是加法运算的结合性规则。但按照我们生成的 AST，变成从右向左了，先计算了“3+4”，然后才跟“2”相加。这可不行！

<p>为什么产生上面的问题呢？是因为我们修改了文法，把文法中加号左右两边的部分调换了一下。造成的影响是什么呢？你可以推导一下“2+3+4”的解析过程：

<li>首先调用乘法表达式匹配函数 multiplicative()，成功，返回了一个字面量节点 2。
<li>接着看看右边是否能递归地匹配加法表达式。
<li>匹配的结果，真的返回了一个加法表达式“3+4”，这个变成了第二个子节点。错误就出在这里了。这样的匹配顺序，“3+4”一定会成为子节点，在求值时被优先计算。
<p>所以，我们前面的方法其实并没有完美地解决左递归，因为它改变了加法运算的结合性规则


<h3>实现表达式求值
<p>上面帮助你理解了“递归”的含义，接下来，我要带你实现表达式的求值。其实，要实现一个表达式计算，只需要基于 AST 做求值运算。这个计算过程比较简单，只需要对这棵树做深度优先的遍历就好了。
<p>
<p>深度优先的遍历也是一个递归算法。以上文中“2 + 3 * 5”的 AST 为例看一下。

<li>对表达式的求值，等价于对 AST 根节点求值。
<li>首先求左边子节点，算出是 2。
<li>接着对右边子节点求值，这时候需要递归计算下一层。计算完了以后，返回是 15（3*5）。
<li>把左右节点相加，计算出根节点的值 17。
<p>代码参见 SimpleCalculator.Java 中的 evaluate() 方法。
<p>
<p>还是以“2+3*5”为例。它的求值过程输出如下，你可以看到求值过程中遍历了整棵树：
 <pre>
    Calculating: AdditiveExp          // 计算根节点
        Calculating: IntLiteral      // 计算第一个子节点
        Result: 2                     // 结果是 2
        Calculating: MulticativeExp   // 递归计算第二个子节点
            Calculating: IntLiteral
            Result: 3
            Calculating: IntLiteral
            Result: 5
        Result: 15                // 忽略递归的细节，得到结果是 15
    Result: 17                    // 根节点的值是 17     </pre>
 


### 04 | 语法分析（二）：解决二元表达式中的难点
<p> 在二元表达式的语法规则中，如果产生式的第一个元素是它自身，那么程序就会无限地递归下去，这种情况就叫做左递归。比如加法表达式的产生式“加法表达式 + 乘法表达式”，就是左递归的。而优先级和结合性则是计算机语言中与表达式有关的核心概念。它们都涉及了语法规则的设计问题。

#### 书写语法规则，并进行推导

<p>书写语法规则，并进行推导
<p>我们已经知道，语法规则是由上下文无关文法表示的，而上下文无关文法是由一组替换规则（又叫产生式）组成的，比如算术表达式的文法规则可以表达成下面这种形式：
 <pre> 
 add -> mul | add + mul
 mul -> pri | mul * pri
 pri -> Id | Num | (add) 
</pre>

<p>这种写法叫做“巴科斯范式”，简称 BNF。Antlr 和 Yacc 这两个工具都用这种写法。为了简化书写，我有时会在课程中把“::=”简化成一个冒号。你看到的时候，知道是什么意思就可以了。
<p>
<p>你有时还会听到一个术语，叫做扩展巴科斯范式 (EBNF)。它跟普通的 BNF 表达式最大的区别，就是里面会用到类似正则表达式的一些写法。比如下面这个规则中运用了 * 号，来表示这个部分可以重复 0 到多次：

#### 确保正确的优先级

<p>掌握了语法规则的写法之后，我们来看看如何用语法规则来保证表达式的优先级。刚刚，我们由加法规则推导到乘法规则，这种方式保证了 AST 中的乘法节点一定会在加法节点的下层，也就保证了乘法计算优先于加法计算。
<p>
<p>听到这儿，你一定会想到，我们应该把关系运算（>、=、<）放在加法的上层，逻辑运算（and、or）放在关系运算的上层
<pre>exp -> or | or = exp   
or -> and | or || and
and -> equal | and && equal
equal -> rel | equal == rel | equal != rel
rel -> add | rel > add | rel < add | rel >= add | rel <= add
add -> mul | add + mul | add - mul 
mul -> pri | mul * pri | mul / pri    </pre>
<p>这里表达的优先级从低到高是：赋值运算、逻辑运算（or）、逻辑运算（and）、相等比较（equal）、大小比较（rel）、加法运算（add）、乘法运算（mul）和基础表达式（pri）。

#### 确保正确的结合性

<p>在上一讲中，我针对算术表达式写的第二个文法是错的，因为它的计算顺序是错的。“2+3+4”这个算术表达式，先计算了“3+4”然后才和“2”相加，计算顺序从右到左，正确的应该是从左往右才对。
<p>
<p>这就是运算符的结合性问题。什么是结合性呢？同样优先级的运算符是从左到右计算还是从右到左计算叫做结合性。我们常见的加减乘除等算术运算是左结合的，“.”符号也是左结合的。
<p>赋值运算就是典型的右结合的例子，比如“x = y = 10”。
<p>
<p>规律：对于左结合的运算符，递归项要放在左边；而右结合的运算符，递归项放在右边。

 #### 消除左递归
 
<p>我提到过左递归的情况，也指出递归下降算法不能处理左递归。这里我要补充一点，并不是所有的算法都不能处理左递归，对于另外一些算法，左递归是没有问题的，比如 LR 算法。
<p>
<p>消除左递归，用一个标准的方法，就能够把左递归文法改写成非左递归的文法。以加法表达式规则为例，原来的文法是“add -> add + mul”，现在我们改写成：
  <pre>
 add -> mul add'
 add' -> + mul add' | ε   </pre>
<p>文法中，ε（读作 epsilon）是空集的意思。
<img  src="https://static001.geekbang.org/resource/image/50/22/50a501fc747b23aa0dca319fa87e6622.jpg">

<p>我们扩展一下话题。在研究递归函数的时候，有一个概念叫做尾递归，尾递归函数的最后一句是递归地调用自身。
<p>
<p>编译程序通常都会把尾递归转化为一个循环语句，使用的原理跟上面的伪代码是一样的。相对于递归调用来说，循环语句对系统资源的开销更低，因此，把尾递归转化为循环语句也是一种编译优化技术。

<p>继续左递归的话题。现在我们知道怎么写这种左递归的算法了，大概是下面的样子：
</pre>
private SimpleASTNode additive(TokenReader tokens) throws Exception {
    SimpleASTNode child1 = multiplicative(tokens);  // 应用 add 规则
    SimpleASTNode node = child1;
    if (child1 != null) {
        while (true) {                              // 循环应用 add'
            Token token = tokens.peek();
            if (token != null && (token.getType() == TokenType.Plus || token.getType() == TokenType.Minus)) {
                token = tokens.read();              // 读出加号
                SimpleASTNode child2 = multiplicative(tokens);  // 计算下级节点
                node = new SimpleASTNode(ASTNodeType.Additive, token.getText());
                node.addChild(child1);              // 注意，新节点在顶层，保证正确的结合性
                node.addChild(child2);
                child1 = node;
            } else {
                break;
            }
        }
    }
    return node;
}  </pre>

### 05 | 语法分析（三）：实现一门简单的脚本语言

#### 增加所需要的语法规则

<p>首先，一门脚本语言是要支持语句的，比如变量声明语句、赋值语句等等。单独一个表达式，也可以视为语句，叫做“表达式语句”。你在终端里输入 2+3；，就能回显出 5 来，这就是表达式作为一个语句在执行。按照我们的语法，无非是在表达式后面多了个分号而已。C 语言和 Java 都会采用分号作为语句结尾的标识，我们也可以这样写。

<p>我们用扩展巴科斯范式（EBNF）写出下面的语法规则：
<pre>
programm: statement+;  
statement
: intDeclaration
| expressionStatement
| assignmentStatement
; </pre>
<p>变量声明语句以 int 开头，后面跟标识符，然后有可选的初始化部分，也就是一个等号和一个表达式，最后再加分号：

<pre>intDeclaration : 'int' Identifier ( '=' additiveExpression)? ';';</pre>
<p>表达式语句目前只支持加法表达式，未来可以加其他的表达式，比如条件表达式，它后面同样加分号：

<pre>expressionStatement : additiveExpression ';';</pre>
<p>赋值语句是标识符后面跟着等号和一个表达式，再加分号：

<pre>assignmentStatement : Identifier '=' additiveExpression ';';</pre>
<p>为了在表达式中可以使用变量，我们还需要把 primaryExpression 改写，除了包含整型字面量以外，还要包含标识符和用括号括起来的表达式：

<pre>primaryExpression : Identifier| IntLiteral | '(' additiveExpression ')';</pre>
<p>这样，我们就把想实现的语法特性，都用语法规则表达出来了。接下来，我们就一步一步实现这些特性。

#### 让脚本语言支持变量

<p>我们简单地用了一个 HashMap 作为变量存储区。在变量声明语句和赋值语句里，都可以修改这个变量存储区中的数据，而获取变量值可以采用下面的代码：
<pre>
if (variables.containsKey(varName)) {
    Integer value = variables.get(varName);  // 获取变量值
    if (value != null) {
        result = value;                      // 设置返回值
    } else {                                 // 有这个变量，没有值
        throw new Exception("variable " + varName + " has not been set any value");
    }
}
else{ // 没有这个变量。
    throw new Exception("unknown variable: " + varName);
}</pre>

#### 解析赋值语句

<p>我们来解析赋值语句，例如“age = age + 10 * 2；”：
<pre>
private SimpleASTNode assignmentStatement(TokenReader tokens) throws Exception {
    SimpleASTNode node = null;
    Token token = tokens.peek();    // 预读，看看下面是不是标识符
    if (token != null && token.getType() == TokenType.Identifier) {
        token = tokens.read();      // 读入标识符
        node = new SimpleASTNode(ASTNodeType.AssignmentStmt, token.getText());
        token = tokens.peek();      // 预读，看看下面是不是等号
        if (token != null && token.getType() == TokenType.Assignment) {
            tokens.read();          // 取出等号
            SimpleASTNode child = additive(tokens);
            if (child == null) {    // 出错，等号右面没有一个合法的表达式
                throw new Exception("invalide assignment statement, expecting an expression");
            }
            else{
                node.addChild(child);   // 添加子节点
                token = tokens.peek();  // 预读，看看后面是不是分号
                if (token != null && token.getType() == TokenType.SemiColon) {
                    tokens.read();      // 消耗掉这个分号
                } else {            // 报错，缺少分号
                    throw new Exception("invalid statement, expecting semicolon");
                }
            }
        }
        else {
            tokens.unread();    // 回溯，吐出之前消化掉的标识符
            node = null;
        }
    }
    return node;
}  </pre>
<p>为了方便你理解，我来解读一下上面这段代码的逻辑：
<p>
<p>我们既然想要匹配一个赋值语句，那么首先应该看看第一个 Token 是不是标识符。如果不是，那么就返回 null，匹配失败。如果第一个 Token 确实是标识符，我们就把它消耗掉，接着看后面跟着的是不是等号。如果不是等号，那证明我们这个不是一个赋值语句，可能是一个表达式什么的。那么我们就要回退刚才消耗掉的 Token，就像什么都没有发生过一样，并且返回 null。回退的时候调用的方法就是 unread()。
<p>如果后面跟着的确实是等号，那么在继续看后面是不是一个表达式，表达式后面跟着的是不是分号。如果不是，就报错就好了。这样就完成了对赋值语句的解析。 

#### 理解递归下降算法中的回溯

<p>尝试一个规则不成功之后，恢复到原样，再去尝试另外的规则，这个现象就叫做“回溯”。
<p>什么时候该回溯，什么时候该提示语法错误？
<p>
<p>大家在阅读示例代码的过程中，应该发现里面有一些错误处理的代码，并抛出了异常。比如在赋值语句中，如果等号后面没有成功匹配一个加法表达式，我们认为这个语法是错的。因为在我们的语法中，等号后面只能跟表达式，没有别的可能性。\
<p>
<p>你可能会意识到一个问题，当我们在算法中匹配不成功的时候，我们前面说的是应该回溯呀，应该再去尝试其他可能性呀，为什么在这里报错了呢？换句话说，什么时候该回溯，什么时候该提示这里发生了语法错误呢？
<p>
<p>其实这两种方法最后的结果是一样的。我们提示语法错误的时候，是说我们知道已经没有其他可能的匹配选项了，不需要浪费时间去回溯。就比如，在我们的语法中，等号后面必然跟表达式，否则就一定是语法错误。你在这里不报语法错误，等试探完其他所有选项后，还是需要报语法错误。所以说，提前报语法错误，实际上是我们写算法时的一种优化。

#### 实现一个简单的 REPL

<p> 这个输入、执行、打印的循环过程就叫做 REPL（Read-Eval-Print Loop）。你可以在 REPL 中迅速试验各种语句，REPL 即时反馈的特征会让你乐趣无穷。
<p>在 SimpleScript.java 中，我们也实现了一个简单的 REPL。基本上就是从终端一行行的读入代码，当遇到分号的时候，就解释执行，代码如下：
<pre>
SimpleParser parser = new SimpleParser();
SimpleScript script = new SimpleScript();
BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));   // 从终端获取输入
String scriptText = "";
System.out.print("\n>");   // 提示符
while (true) {             // 无限循环
    try {
        String line = reader.readLine().trim(); // 读入一行
        if (line.equals("exit();")) {   // 硬编码退出条件
            System.out.println("good bye!");
            break;
        }
        scriptText += line + "\n";
        if (line.endsWith(";")) { // 如果没有遇到分号的话，会再读一行
            ASTNode tree = parser.parse(scriptText); // 语法解析
            if (verbose) {
                parser.dumpAST(tree, "");
            }
            script.evaluate(tree, ""); // 对 AST 求值，并打印
            System.out.print("\n>");   // 显示一个提示符
            scriptText = "";
        }
    } catch (Exception e) { // 如果发现语法错误，报错，然后可以继续执行
        System.out.println(e.getLocalizedMessage());
        System.out.print("\n>");   // 提示符
        scriptText = "";
    } 
}   </pre>

