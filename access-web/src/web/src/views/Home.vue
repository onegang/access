<template>
  <div class="home">
    <div>
    </div>
    <WorkflowButtons />
    <UsersTable />
  </div>
</template>

<script>
// @ is an alias to /src
import UsersTable from '../components/home/UsersTable.vue';
import WorkflowButtons from '../components/home/WorkflowButtons.vue';
import {mapGetters} from 'vuex';

export default {
  name: 'Home',
  components: {
    UsersTable,
    WorkflowButtons,
  },
  computed: {
    // stage() {
    //   return this.$store.getters.WORKFLOW.stage;
    // },
    ...mapGetters(['STAGE']),
  },
  methods: {
    
  },
};
</script>

<style scoped>
  
</style>
