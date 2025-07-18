import API from "@/config/axios/axiosInstance.js";

const user = {
    //命名空间
    namespaced: true,
    //提供唯一的公共数据源，所有共享的数据统一放到store的state进行储存，相似与data
    // $store.state.命名空间.变量名
    state: {
        // tag:{
        //     Separator: /,|、|，|[ ]/
        // }
        // webTitle: ""
        token: "",
    },
    //里面定义方法，操作state方法
    //更改 Vuex 的 store 中的状态的唯一方法是提交 mutation。
    //Vuex 中的 mutation 非常类似于事件：每个 mutation 都有一个字符串的事件类型 (type)和一个回调函数 (handler)。这个回调函数就是我们实际进行状态更改的地方，并且它会接受 state 作为第一个参数：
    // this.$store.commit("命名空间/方法名");
    mutations: {
        // changeWebTitle(state, title){
        //     state.webTitle = title
        // }
        setToken(state, token){
            console.log("保存Token")
            state.token = token;
            localStorage.setItem("token", token)
        },
        clearToken(state){
            console.log("清空Token")
            state.token = "";
            localStorage.setItem("token", "")
        }
    },
    // 操作异步操作mutation
    //Action和Mutation相似，一般不用Mutation 异步操作，若要进行异步操作，使用Action
    // 原因：为了方便devtools打个快照存下来，方便管理维护。所以说这个只是规范，而不是逻辑的不允许，只是为了让这个工具能够追踪数据变化而已
    // this.$store.dispatch("命名空间/方法名")
    actions: {
        // demo({ state, commit, dispatch }){}
        async checkToken({ state, commit, dispatch }){
            if(!state.token){
                // console.log("checkToken 0001", state.token)
                let storage = localStorage.getItem("token");
                if(storage){
                    state.token = storage
                }
                // console.log("checkToken 0002", storage)
            }
            // console.log("checkToken 0003", state.token)
            if(!state.token){
                return false;
            }
            // console.log("checkToken 0004")
            //console.log("1")
            let result = false;
            try {
                result = await API.post("/secure/check", {"token": state.token}).then(res => {
                    let data = API.isSucess(res);
                    console.log("2", !!data, data, res)
                    //return !!data;
                    if(data){
                        if(res.data.data){
                            console.log("更新Token");
                            state.token = res.data.data;
                            localStorage.setItem("token", state.token)
                        }
                        return true;
                    }
                    return false;
                });
            } catch (e) {
                console.log("checkToken 出现报错", e)
                result = false;
            }
            //console.log("3")
            return result;
        }
    },
    //类似于vue中的computed，进行缓存，对于Store中的数据进行加工处理形成新的数据
    // this.$store.getters["命名空间/方法名"](传参)
    Getter: {},
    //当遇见大型项目时，数据量大，store就会显得很臃肿
    // 为了解决以上问题，Vuex 允许我们将 store 分割成模块（module）。每个模块拥有自己的 state、mutation、action、getter、甚至是嵌套子模块——从上至下进行同样方式的分割：
    modules: {}
}
export default user;