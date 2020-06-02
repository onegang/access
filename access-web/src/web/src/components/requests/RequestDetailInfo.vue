<template>
  <v-container>
    <div v-if="request.purpose" class="py-1">
      <span class="subtitle-2">Purpose: </span>{{request.purpose}}
    </div>
    <div class="py-1">
      <span class="subtitle-2">Effective Date: </span>{{moment(request.effectiveDate).format('YYYY-MM-DD')}}
    </div>
    <div v-if="request.expiryDate" class="py-1">
      <span class="subtitle-2">Expiry Date: </span>{{request.expiryDate}}
    </div>
    <div v-if="request.comments" class="py-1">
      <span class="subtitle-2">Comments: </span>{{request.comments}}
    </div>
    <!-- <div v-if="request.attachments.length>0">Supporting Documents: {{request.attachments.map(file => file.name).join(", ")}}</div> -->
    <div class="inline py-1">
      <span class="subtitle-2">Supporters: </span>
      <v-chip v-for="(user, index) in request.supporters" :key="`s-${index}`"
        class="mx-1" label small
        :outlined="isPending(user)"
        :color="getColor(user)">
        <v-avatar left><v-icon>mdi-account</v-icon></v-avatar>
        {{user.name}}: {{user.status}}
      </v-chip>
    </div>

    <div class="inline py-1">
      <span class="subtitle-2">Approvers: </span>
      <v-chip v-for="(user, index) in request.approvers" :key="`s-${index}`"
        class="mx-1" label small
        :outlined="isPending(user)"
        :color="getColor(user)">
        <v-avatar left><v-icon>mdi-account</v-icon></v-avatar>
        {{user.name}}: {{user.status}}
      </v-chip>
    </div>
    

    <h3 class="pt-10">Access Changes</h3>
    <v-divider class="pb-3" />
    <div v-if="request.manual" class="py-1">
      {{request.manual}}
    </div>
    <!-- <v-skeleton-loader v-if="CHANGES===null" type="paragraph" />
    <div v-if="!hasChanges">No changes!</div>
    <div v-if="REQUESTFORM.manual">{{REQUESTFORM.manual}}</div>
    <div v-if="CHANGES && CHANGES.added">
      <ChangeItem v-for="(added, index) in CHANGES.added" :key="`add-${index}`" :change="added" type="ADD" />
    </div>
    <div v-if="CHANGES && CHANGES.removed">
      <ChangeItem v-for="(removed, index) in CHANGES.removed" :key="`remove-${index}`" :change="removed" type="REMOVE" />
    </div> -->

    <h3 class="pt-10">Effective User Access</h3>
    <v-divider class="pb-3" />
    <div v-for="user of request.users" v-bind:key="user.name">
        <UserDetails :user="user" readonly />
    </div>
    <v-divider class="pb-3" />
    <v-btn small color="error">Cancel Request</v-btn>
  </v-container>
</template>

<script>
// import UserDetails from '../home/UserDetails.vue';
// import ChangeItem from '../home/ChangeItem.vue';

// console.log(this.request);

export default {
  components: {
    // UserDetails,
    // ChangeItem,
  },
  props: {
    request: {
      type: Object,
      required: true,
    }
  },
  computed: {
    // selectedUsers() {
    //   return this.USERS.filter(user => user.selected);
    // },
    // hasChanges() {
    //   if(this.CHANGES===null)
    //     return false;
    //   return this.CHANGES.added.length>0 || this.CHANGES.removed.length>0 || this.REQUESTFORM.manual;
    // },
    // ...mapGetters(['REQUESTDETAIL']),
  },
  methods:{
    isPending(user) {
        return user.status==="PENDING";
      },
      getColor(user) {
        if(user.status==="REJECTED")
          return "deep-orange darken-3 white--text";
        else if(user.status==="APPROVED")
          return "info"
        else
          return "";
      },
  }
};
</script>

<style>
  
</style>
