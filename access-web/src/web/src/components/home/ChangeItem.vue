<template>
  <div>
    <strong :class="color">{{type}} </strong> 
    <span v-if="change.roles.length==1">role</span>
    <span v-if="change.roles.length>1">{{change.roles.length}} roles</span> 
    <v-chip v-for="(role, index) in change.roles" :key="index"
      class="mx-1"
      label
      outlined
      small
      :color="color"
    >{{role}}</v-chip> 
    {{type==="ADD"?"to":"from"}}
    {{change.usernames.join(", ")}}
  </div>
</template>

<script>

export default {
  props: {
    change: {
      type: Object,
      required: true,
    },
    type: {
      type: String,
      required: true,
    }
  },
  computed: {
    color() {
      if(this.type==="ADD")
        return "indigo--text";
      else
        return "red--text";
    }
  },
};
</script>

<style scoped>
  
</style>
