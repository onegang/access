<template>
  <div>
    <strong :class="color">{{type}} </strong> 
    <span v-if="change.roles.length==1">role</span>
    <span v-if="change.roles.length>1">{{change.roles.length}} roles</span> 
    <v-chip v-for="(role, index) in change.roles" :key="`r-${index}`"
      :color="color"
      class="mx-1" label outlined small>
      {{role}}
    </v-chip> 
    {{type==="ADD"?"to":"from"}}
    <v-chip v-for="(name, index) in change.usernames" :key="`u-${index}`"
      class="mx-1" label outlined small>
      <v-avatar left><v-icon>mdi-account-outline</v-icon></v-avatar>
      {{name}}
    </v-chip>
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
  .underline {
    text-decoration: underline;
  }
</style>
