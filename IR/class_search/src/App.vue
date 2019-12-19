<template>
  <div id="app">
    <div class="search">
      <el-input style="width: 300px;" v-model="search" />
      <el-button style="margin-left:20px" type="primary" @click="getDetail">搜索</el-button>
    </div>
     <el-table :data="course" style="width: 800px; margin:0 auto;">
      <el-table-column prop="course_code" sortable label="课程号" width="200">
      </el-table-column>
      <el-table-column prop="name" sortable label="课程名称" width="500">
      </el-table-column>
      <el-table-column prop="credit" sortable label="课程学分" width="100">
      </el-table-column>
     </el-table>
  </div>
</template>

<script>

export default {
  name: 'app',
  data(){
    return{
      course:[],
      search:''
    }
  },
  methods:{
    getDetail(){
      if(this.search == ''){
        this.$alert('请输入搜索内容！', '警告', {
          confirmButtonText: '确定',
          callback: action => {
            this.$message({
              type: 'info',
              message: `action: ${ action }`
            });
          }
        });
      }
      else{
        fetch(`/api/search/course/?keyword=${this.search}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json"
            }
        }).then(res => {
            return res.json();
        }).then(res => {
            this.course = res
        })
      }
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
